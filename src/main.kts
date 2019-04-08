import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.system.exitProcess

val file = File("cat1.wav")
val fileExits = file.exists()

if(!fileExits){
    exitProcess(-1)
}

// ファイルを読み込むことができるか

val fileStream = file.readBytes()

if(fileStream.isEmpty()){
    exitProcess(-2)
}

fun ByteArray.copyOfRange(target: Pair<Int,Int >) = this.copyOfRange(target.first, target.second)

fun ByteArray.getTargetBytes( kind: WaveFileElement) = this.copyOfRange(kind.getStartAndFinish())
fun ByteArray.toConcatString() = this.map{it.toChar()}.fold(""){ r, i -> r + i }
fun ByteArray.toIntByLittleEndian() = ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN).getInt()

fun WaveFileElement.getStartAndFinish(): Pair<Int,Int> =
    when(this){
        WaveFileElement.ChunkID -> Pair(0,4)
        WaveFileElement.ChunkSize -> Pair(4,8),
        WaveFileElement.Format-> Pair(8,11),

    }

enum class WaveFileElement{
    ChunkID,
    ChunkSize,
    Format
}


// ChunkID
val chunkId = fileStream.getTargetBytes(WaveFileElement.ChunkID).toConcatString()
println("chunkIDString: ${chunkId}")

// ChunkSize
val chunkSize = fileStream.getTargetBytes(WaveFileElement.ChunkSize).toIntByLittleEndian()
println("chunkSize: ${chunkSize}")

// Format
val format = fileStream.getTargetBytes(WaveFileElement.Format).toConcatString()
println("format: ${format}")

// SubSunkId
val subChunkId = fileStream.copyOfRange(12,15).map { it.toChar() }.fold("",{r,i -> r + i})
println("subChunkID: ${subChunkId}")

// SubChunSize
val subChunkSizeStream = fileStream.copyOfRange(15,19)
val subChunkSize = ByteBuffer.wrap(subChunkSizeStream).order(ByteOrder.LITTLE_ENDIAN).getInt()
println("SubChukSize: ${subChunkSize}")

// Audio Format
val audioFormat = fileStream.copyOfRange(20,22).elementAt(0)
val audioFormatString = when(audioFormat){
    1.toByte() -> "PCM"
    else -> "No infomation for audio format"
}
println("Audio format: ${audioFormatString}")

// チャンネル数
val audioNumber = fileStream.copyOfRange(22,24).elementAt(0)
val audioNumberString = when(audioNumber){
    1.toByte() -> "Mono"
    2.toByte() -> "Steleo"
    else -> "No audio Number Infomation"
}

println("音の再生チャネル数は: ${audioNumberString}")

val samplelingRateStream =  fileStream.copyOfRange(24, 28)
val samplingRate = ByteBuffer.wrap(samplelingRateStream).order(ByteOrder.LITTLE_ENDIAN).getInt()
println("SamplingRate: ${samplingRate} Hz")