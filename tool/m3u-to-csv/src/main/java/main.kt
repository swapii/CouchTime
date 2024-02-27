import m3u.parseM3uPlaylist
import java.io.File

fun main(vararg args: String) {

    println(File(".").absolutePath)

    val playlistFile = File(args.first())

    playlistFile.bufferedReader().lineSequence()
        .parseM3uPlaylist()
        .forEach {
            print(it.extInf.displayTitle)
            print(',')
            print(it.extGrp)
            print(',')
            print(it.address)
            println()
        }

}
