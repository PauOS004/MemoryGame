import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.Serializable

data class Achievement(
    val title: String,
    val description: String,
) : Serializable {
    var unlocked by mutableStateOf(false)
}
