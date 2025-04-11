import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class InventoryFrameworkExtension @Inject constructor(objects: ObjectFactory) {

    val publish: Property<Boolean> = objects.property<Boolean>().convention(false)

    val generateVersionFile: Property<Boolean> = objects.property<Boolean>().convention(false)
}