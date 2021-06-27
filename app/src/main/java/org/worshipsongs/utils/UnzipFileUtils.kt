package org.worshipsongs.utils

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.util.ArrayList
import java.util.zip.ZipFile

object UnzipUtils
{

    fun getSongs(serviceFilePath: String): MutableList<String>
    {
        var songs: MutableList<String> = ArrayList()
        val serviceDir = File(serviceFilePath)
        val readFileToString = FileUtils.readFileToString(serviceDir)
        var rootJsonArray = JSONArray(readFileToString)
        for (i in 0 until rootJsonArray.length())
        {
            val rootJsonObject = rootJsonArray.getJSONObject(i)
            val serviceItemObject = jsonObject(rootJsonObject, "serviceitem");
            if (serviceItemObject != null)
            {
                val headerObject = serviceItemObject.getJSONObject("header")
                val title = headerObject.getString("title")
                songs.add(title)
            }
        }
        return songs
    }

    private fun jsonObject(rootJsonObject: JSONObject, key: String): JSONObject?
    {
        try
        {
            return rootJsonObject.getJSONObject(key)
        } catch (e: Exception)
        {
            return null
        }
    }

    fun getServiceNames(serviceDirPath: String): MutableList<String>
    {
        var services: MutableList<String> = ArrayList()
        val serviceDir = File(serviceDirPath)
        for (serviceFile in serviceDir.listFiles())
        {
            services.add(serviceFile.name)
        }
        return services
    }

    fun unZipServices(serviceDir: File, destinationDir: File)
    {

        for (serviceFile in serviceDir.listFiles())
        {
            unzip(serviceFile, destinationDir, true)
        }
    }

    /**
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    @Throws(IOException::class)
    fun unzip(zipFilePath: File, destDirectory: File, fileNameFromDir: Boolean)
    {
        // val destDir = File(destDirectory)
        if (!destDirectory.exists())
        {
            destDirectory.mkdir()
        }
        ZipFile(zipFilePath).use { zip ->

            zip.entries().asSequence().forEach { entry ->

                zip.getInputStream(entry).use { input ->


                    var filePath = destDirectory.absolutePath + File.separator + entry.name
                    if (!entry.isDirectory)
                    {
                        if (fileNameFromDir)
                        {
                            filePath = destDirectory.absolutePath + File.separator + FilenameUtils.getBaseName(zipFilePath.absolutePath)
                        }
                        // if the entry is a file, extracts it
                        extractFile(input, filePath)
                    } else
                    {
                        // if the entry is a directory, make the directory
                        val dir = File(filePath)
                        dir.mkdir()
                    }

                }

            }
        }
    }

    /**
     * Extracts a zip entry (file entry)
     * @param inputStream
     * @param destFilePath
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun extractFile(inputStream: InputStream, destFilePath: String)
    {
        val bos = BufferedOutputStream(FileOutputStream(destFilePath))
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read: Int
        while (inputStream.read(bytesIn).also { read = it } != -1)
        {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }

    /**
     * Size of the buffer to read/write data
     */
    private const val BUFFER_SIZE = 4096

}