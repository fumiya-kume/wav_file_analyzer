import java.io.File
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

// ChunkID
val chunkID = fileStream.copyOfRange(0,4)
val chunkIDString = chunkID.map { it.toChar() }.fold(""){ r, i -> r + i }
println("chunkIDString: ${chunkIDString}")

// ChunkSize
val chunkSizeBytes = fileStream.copyOfRange(4,8)
val chunkSizeInt = ByteBuffer.wrap(chunkSizeBytes).order(ByteOrder.LITTLE_ENDIAN).getInt()
println("chunkSize: ${chunkSizeInt}")

// Format
val format = fileStream.copyOfRange(8,11).map { it.toChar() }.fold("",{r,i -> r + i})
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