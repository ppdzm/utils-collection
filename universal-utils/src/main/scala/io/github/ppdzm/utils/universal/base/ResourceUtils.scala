package io.github.ppdzm.utils.universal.base

import java.io.{File, InputStream}
import java.net.URL

import io.github.ppdzm.utils.universal.feature.ExceptionGenerator


object ResourceUtils extends Logging {

    def locateFile(fileName: String, classpathFirst: Boolean): String = {
        if (classpathFirst) {
            locateInClassPath(fileName, locateInFs(fileName, null))
        } else {
            locateInFs(fileName, locateInClassPath(fileName, null))
        }
    }

    def locateInClassPath(fileName: String, elseWhat: => String): String = {
        val url = Thread.currentThread().getContextClassLoader.getResource(fileName)
        if (url != null)
            url.getPath
        else
            elseWhat
    }

    def locateInFs(fileName: String, elseWhat: => String): String = {
        if (new File(fileName).exists())
            fileName
        else
            elseWhat
    }

    def locateAsInputStream(fileName: String): InputStream = {
        val url = locateResourceAsURL(fileName)
        if (url == null) {
            throw ExceptionGenerator.newException("ResourceNotFound", s"resource $fileName not found")
        }
        logInfo(s"file located at ${url.getPath}")
        url.openStream()
    }

    def locateResourceAsURL(fileName: String): URL = {
        val url = Thread.currentThread().getContextClassLoader.getResource(fileName)
        if (url == null) {
            val file = new File(fileName)
            if (file.exists())
                return new URL("file", "", -1, file.getAbsolutePath)
        }
        url
    }

}
