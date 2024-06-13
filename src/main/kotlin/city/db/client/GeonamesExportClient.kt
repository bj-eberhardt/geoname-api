package city.db.client

import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client

@Client("geonames")
interface GeonamesExportClient {
    @Get("/export/zip/{name}", consumes = ["application/zip"])
    fun downloadExport(name: String): ByteArray
}
