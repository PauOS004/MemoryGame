import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

enum class DeviceType { PHONE, TABLET }

@Composable
fun getDeviceType(): DeviceType {
    val configuration = LocalConfiguration.current
    return if (configuration.screenWidthDp >= 600) DeviceType.TABLET else DeviceType.PHONE
}
