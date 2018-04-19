package de.kuschku.quasseldroid.util.helper

import org.apache.commons.codec.digest.DigestUtils
import java.security.cert.CertificateExpiredException
import java.security.cert.CertificateNotYetValidException
import java.security.cert.X509Certificate

val X509Certificate.isValid: Boolean
  get() = try {
    checkValidity()
    true
  } catch (e: CertificateExpiredException) {
    false
  } catch (e: CertificateNotYetValidException) {
    false
  }

val X509Certificate.fingerprint: String
  get() = DigestUtils.sha1(encoded).joinToString(":") {
    (it.toInt() and 0xff).toString(16)
  }

val javax.security.cert.X509Certificate.fingerprint: String
  get() = DigestUtils.sha1(encoded).joinToString(":") {
    (it.toInt() and 0xff).toString(16)
  }
