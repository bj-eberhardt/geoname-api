package city.db

import city.db.client.GeonamesExportClient
import city.db.entity.City
import city.db.repository.CityRepository
import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.CsvToBeanBuilder
import io.micronaut.context.annotation.Context
import io.micronaut.data.exceptions.DataAccessException
import io.micronaut.scheduling.annotation.Async
import io.micronaut.transaction.annotation.Transactional
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files
import java.sql.SQLIntegrityConstraintViolationException
import java.util.stream.Stream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeBytes

private val FILES =
    listOf(
        "allCountries.zip",
        "NL_full.csv.zip",
        "GB_full.csv.zip",
        "CA_full.csv.zip",
    )

private val LOGGER = LoggerFactory.getLogger(UpdateService::class.java)

@Context
open class UpdateService(
    private val geonamesExportClient: GeonamesExportClient,
    private val cityRepository: CityRepository,
) {
    @Async
    @Transactional
    open fun updateDatabase() {
        // clear db first
        LOGGER.info("Deleting the database right now.")
        cityRepository.deleteAll()

        var addedEntries = 0L
        FILES.forEach { file ->
            addedEntries += downloadFileAndImport(file)
        }
        LOGGER.info("Added $addedEntries entries to the database.")
    }

    private fun downloadFileAndImport(filename: String): Long {
        val tmpFile = Files.createTempFile("geo-city", ".zip")
        try {
            val zipContent = geonamesExportClient.downloadExport(filename)
            LOGGER.info("Downloading $filename. Parsing it now.")
            tmpFile.writeBytes(zipContent)

            ZipFile(tmpFile.toFile()).use { zipFile ->
                val validFiles =
                    zipFile
                        .entries()
                        .asSequence()
                        .filter { !it.name.contains("README", true) && it.name.endsWith(".txt", true) }
                        .toList()
                return validFiles.sumOf { entry ->
                    processZipFileEntry(zipFile, entry)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            tmpFile.deleteIfExists()
        }
        return 0L
    }

    private fun processZipFileEntry(
        zipFile: ZipFile,
        entry: ZipEntry,
    ): Long {
        zipFile.getInputStream(entry).use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                return parseCsvContents(reader)
                    .map {
                        City(
                            name = it.placeName,
                            stateCode = it.adminCode1,
                            stateName = it.adminName1,
                            latitude = it.latitude.toBigDecimal(),
                            longitude = it.longitude.toBigDecimal(),
                            countryCode = it.countryCode,
                        )
                    }.map {
                        saveInDatabase(it)
                    }.filter { it }
                    .count()
            }
        }
    }

    private fun saveInDatabase(it: City): Boolean {
        try {
            cityRepository.save(it)
            return true
        } catch (e: SQLIntegrityConstraintViolationException) {
            // ignore
        } catch (e: DataAccessException) {
            // ignore
        }
        return false
    }

    private fun parseCsvContents(reader: InputStreamReader): Stream<CsvContent> {
        val strategy =
            ColumnPositionMappingStrategy<CsvContent>().apply {
                type = CsvContent::class.java
                setColumnMapping(
                    "countryCode",
                    "postalCode",
                    "placeName",
                    "adminName1",
                    "adminCode1",
                    "adminName2",
                    "adminCode2",
                    "adminName3",
                    "adminCode3",
                    "latitude",
                    "longitude",
                    "accuracy",
                )
            }

        val csvToBean =
            CsvToBeanBuilder<CsvContent>(reader)
                .withType(CsvContent::class.java)
                .withIgnoreLeadingWhiteSpace(true)
                .withSkipLines(0)
                .withSeparator('\t')
                .withMappingStrategy(strategy)
                .build()

        return csvToBean.stream()
    }
}

data class CsvContent(
    var countryCode: String,
    var postalCode: String,
    var placeName: String,
    var adminName1: String,
    var adminCode1: String,
    var adminName2: String,
    var adminCode2: String,
    var adminName3: String,
    var adminCode3: String,
    var latitude: Double,
    var longitude: Double,
    var accuracy: String,
) {
    constructor() : this(
        "", "", "", "", "",
        "," +
            "",
        "", "", "", 0.0, 0.0, "",
    )
}
