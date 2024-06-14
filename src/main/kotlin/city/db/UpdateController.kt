package city.db

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.swagger.v3.oas.annotations.Operation

@Controller("/database")
open class UpdateController(
    private val updateService: UpdateService,
) {
    @Operation(
        summary = "Updates the database by downloading new geonames databases and importing them.",
        description =
            "This is starting the update job in background. It takes about 1h20min and will" +
                " add it to the database in transaction.",
    )
    @Post("update")
    open fun updateDatabase(): String {
        updateService.updateDatabase()
        return """{"action":"triggered asynchronously"}"""
    }
}
