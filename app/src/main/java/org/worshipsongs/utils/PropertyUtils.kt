package org.worshipsongs.utils

import android.content.Context
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.worshipsongs.CommonConstants

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.ArrayList
import java.util.Enumeration
import java.util.HashMap
import java.util.Properties

/**
 * author:Seenivasan
 * author:Madasamy
 * version:1.0.0
 */
object PropertyUtils
{
    private val CLASS_NAME = PropertyUtils::class.java.simpleName

    fun getPropertyFile(context: Context, propertyFileName: String): File?
    {
        var commonPropertyFile: File? = null
        try
        {
            val configDirPath = "/data/data/" + context.applicationContext.packageName + "/databases/config"
            val configDir = File(configDirPath)
            commonPropertyFile = File(configDir, propertyFileName)
            if (!commonPropertyFile.exists())
            {
                FileUtils.touch(commonPropertyFile)
            }
        } catch (ex: Exception)
        {
            Log.e("PropertyUtils", "Error$ex")
        }

        return commonPropertyFile
    }

    fun getProperties(propertyFile: File): Properties
    {
        val properties = Properties()
        var inputStream: InputStream? = null
        try
        {
            inputStream = FileInputStream(propertyFile)
            properties.load(inputStream)
            return properties
        } catch (ex: Exception)
        {
            return properties
        }

    }

    fun getProperty(key: String, propertiesFile: File): String
    {
        val properties = Properties()
        var inputStream: InputStream? = null
        try
        {
            inputStream = FileInputStream(propertiesFile)
            properties.load(inputStream)
            return properties[key]!!.toString()
        } catch (ex: Exception)
        {
            return ""
        }

    }

    fun setProperties(propertiesMap: Map<String, String>, propertiesFile: File)
    {
        val properties = getProperties(propertiesFile)
        var outputStream: OutputStream? = null
        try
        {
            outputStream = FileOutputStream(propertiesFile)
            for (key in propertiesMap.keys)
            {
                properties.setProperty(key, propertiesMap[key])
            }
            properties.store(outputStream, "")
        } catch (ex: Exception)
        {
        }

    }

    fun setProperty(key: String, value: String, propertiesFile: File)
    {
        val propertyMap = HashMap<String, String>()
        propertyMap[key] = value
        setProperties(propertyMap, propertiesFile)
    }

    fun removeProperty(key: String, propertiesFile: File)
    {
        try
        {
            val properties = Properties()
            val fileInputStream = FileInputStream(propertiesFile)
            properties.load(fileInputStream)
            fileInputStream.close()
            if (properties.remove(key) != null)
            {
                val out = FileOutputStream(propertiesFile)
                properties.store(out, "")
                out.close()
            }
        } catch (ex: Exception)
        {
        }

    }

    fun appendColoredText(tv: TextView, text: String, color: Int)
    {
        val start = tv.text.length
        tv.append(text)
        tv.append("\n")
        val end = tv.text.length
        val spannableText = tv.text as Spannable
        spannableText.setSpan(ForegroundColorSpan(color), start, end, 0)
    }

    fun getServices(propertyFileName: File): List<String>
    {
        val services = ArrayList<String>()
        var inputStream: InputStream? = null
        try
        {
            val property = Properties()
            inputStream = FileInputStream(propertyFileName)
            property.load(inputStream)
            val enumeration = property.propertyNames()
            while (enumeration.hasMoreElements())
            {
                val key = enumeration.nextElement() as String
                services.add(key)
            }
            inputStream.close()

        } catch (ex: Exception)
        {
            Log.e(PropertyUtils::class.java.simpleName, "Error occurred while reading services", ex)
        }

        return services
    }

    fun removeSong(propertyFile: File, serviceName: String, song: String): Boolean
    {
        try
        {
            var propertyValue = ""
            println("Preparing to remove service:$song")
            val property = PropertyUtils.getProperty(serviceName, propertyFile)
            val propertyValues = property.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            println("File:" + propertyFile.absolutePath)
            for (i in propertyValues.indices)
            {
                println("Property length: " + propertyValues.size)
                Log.i(CLASS_NAME, "Property value  " + propertyValues[i])
                if (StringUtils.isNotBlank(propertyValues[i]) && !propertyValues[i].equals(song, ignoreCase = true))
                {
                    Log.i(CLASS_NAME, "Append property value" + propertyValues[i])
                    propertyValue = propertyValue + propertyValues[i] + ";"
                }
            }
            Log.i(CLASS_NAME, "Property value after removed  $propertyValue")
            PropertyUtils.setProperty(serviceName, propertyValue, propertyFile)
            return true
        } catch (e: Exception)
        {
            return false
        }

    }

}
