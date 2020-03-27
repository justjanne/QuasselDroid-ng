/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.util.backport

object OsConstants {
  const val EPERM = 1
  const val ENOENT = 2
  const val ESRCH = 3
  const val EINTR = 4
  const val EIO = 5
  const val ENXIO = 6
  const val E2BIG = 7
  const val ENOEXEC = 8
  const val EBADF = 9
  const val ECHILD = 10
  const val EAGAIN = 11
  const val ENOMEM = 12
  const val EACCES = 13
  const val EFAULT = 14
  const val ENOTBLK = 15
  const val EBUSY = 16
  const val EEXIST = 17
  const val EXDEV = 18
  const val ENODEV = 19
  const val ENOTDIR = 20
  const val EISDIR = 21
  const val EINVAL = 22
  const val ENFILE = 23
  const val EMFILE = 24
  const val ENOTTY = 25
  const val ETXTBSY = 26
  const val EFBIG = 27
  const val ENOSPC = 28
  const val ESPIPE = 29
  const val EROFS = 30
  const val EMLINK = 31
  const val EPIPE = 32
  const val EDOM = 33
  const val ERANGE = 34
  const val EDEADLK = 35
  const val ENAMETOOLONG = 36
  const val ENOLCK = 37
  const val ENOSYS = 38
  const val ENOTEMPTY = 39
  const val ELOOP = 40
  const val EWOULDBLOCK = 11
  const val ENOMSG = 42
  const val EIDRM = 43
  const val ECHRNG = 44
  const val EL2NSYNC = 45
  const val EL3HLT = 46
  const val EL3RST = 47
  const val ELNRNG = 48
  const val EUNATCH = 49
  const val ENOCSI = 50
  const val EL2HLT = 51
  const val EBADE = 52
  const val EBADR = 53
  const val EXFULL = 54
  const val ENOANO = 55
  const val EBADRQC = 56
  const val EBADSLT = 57
  const val EDEADLOCK = 35
  const val EBFONT = 59
  const val ENOSTR = 60
  const val ENODATA = 61
  const val ETIME = 62
  const val ENOSR = 63
  const val ENONET = 64
  const val ENOPKG = 65
  const val EREMOTE = 66
  const val ENOLINK = 67
  const val EADV = 68
  const val ESRMNT = 69
  const val ECOMM = 70
  const val EPROTO = 71
  const val EMULTIHOP = 72
  const val EDOTDOT = 73
  const val EBADMSG = 74
  const val EOVERFLOW = 75
  const val ENOTUNIQ = 76
  const val EBADFD = 77
  const val EREMCHG = 78
  const val ELIBACC = 79
  const val ELIBBAD = 80
  const val ELIBSCN = 81
  const val ELIBMAX = 82
  const val ELIBEXEC = 83
  const val EILSEQ = 84
  const val ERESTART = 85
  const val ESTRPIPE = 86
  const val EUSERS = 87
  const val ENOTSOCK = 88
  const val EDESTADDRREQ = 89
  const val EMSGSIZE = 90
  const val EPROTOTYPE = 91
  const val ENOPROTOOPT = 92
  const val EPROTONOSUPPORT = 93
  const val ESOCKTNOSUPPORT = 94
  const val EOPNOTSUPP = 95
  const val EPFNOSUPPORT = 96
  const val EAFNOSUPPORT = 97
  const val EADDRINUSE = 98
  const val EADDRNOTAVAIL = 99
  const val ENETDOWN = 100
  const val ENETUNREACH = 101
  const val ENETRESET = 102
  const val ECONNABORTED = 103
  const val ECONNRESET = 104
  const val ENOBUFS = 105
  const val EISCONN = 106
  const val ENOTCONN = 107
  const val ESHUTDOWN = 108
  const val ETOOMANYREFS = 109
  const val ETIMEDOUT = 110
  const val ECONNREFUSED = 111
  const val EHOSTDOWN = 112
  const val EHOSTUNREACH = 113
  const val EALREADY = 114
  const val EINPROGRESS = 115
  const val ESTALE = 116
  const val EUCLEAN = 117
  const val ENOTNAM = 118
  const val ENAVAIL = 119
  const val EISNAM = 120
  const val EREMOTEIO = 121
  const val EDQUOT = 122
  const val ENOMEDIUM = 123
  const val EMEDIUMTYPE = 124
  const val ECANCELED = 125
  const val ENOKEY = 126
  const val EKEYEXPIRED = 127
  const val EKEYREVOKED = 128
  const val EKEYREJECTED = 129
  const val EOWNERDEAD = 130
  const val ENOTRECOVERABLE = 131
  const val ERFKILL = 132
  const val EHWPOISON = 133
  const val ENOTSUP = 95

  @Suppress("DUPLICATE_LABEL_IN_WHEN")
  fun errnoName(errno: Int) = when (errno) {
    EPERM           -> "EPERM"
    ENOENT          -> "ENOENT"
    ESRCH           -> "ESRCH"
    EINTR           -> "EINTR"
    EIO             -> "EIO"
    ENXIO           -> "ENXIO"
    E2BIG           -> "E2BIG"
    ENOEXEC         -> "ENOEXEC"
    EBADF           -> "EBADF"
    ECHILD          -> "ECHILD"
    EAGAIN          -> "EAGAIN"
    ENOMEM          -> "ENOMEM"
    EACCES          -> "EACCES"
    EFAULT          -> "EFAULT"
    ENOTBLK         -> "ENOTBLK"
    EBUSY           -> "EBUSY"
    EEXIST          -> "EEXIST"
    EXDEV           -> "EXDEV"
    ENODEV          -> "ENODEV"
    ENOTDIR         -> "ENOTDIR"
    EISDIR          -> "EISDIR"
    EINVAL          -> "EINVAL"
    ENFILE          -> "ENFILE"
    EMFILE          -> "EMFILE"
    ENOTTY          -> "ENOTTY"
    ETXTBSY         -> "ETXTBSY"
    EFBIG           -> "EFBIG"
    ENOSPC          -> "ENOSPC"
    ESPIPE          -> "ESPIPE"
    EROFS           -> "EROFS"
    EMLINK          -> "EMLINK"
    EPIPE           -> "EPIPE"
    EDOM            -> "EDOM"
    ERANGE          -> "ERANGE"
    EDEADLK         -> "EDEADLK"
    ENAMETOOLONG    -> "ENAMETOOLONG"
    ENOLCK          -> "ENOLCK"
    ENOSYS          -> "ENOSYS"
    ENOTEMPTY       -> "ENOTEMPTY"
    ELOOP           -> "ELOOP"
    EWOULDBLOCK     -> "EWOULDBLOCK"
    ENOMSG          -> "ENOMSG"
    EIDRM           -> "EIDRM"
    ECHRNG          -> "ECHRNG"
    EL2NSYNC        -> "EL2NSYNC"
    EL3HLT          -> "EL3HLT"
    EL3RST          -> "EL3RST"
    ELNRNG          -> "ELNRNG"
    EUNATCH         -> "EUNATCH"
    ENOCSI          -> "ENOCSI"
    EL2HLT          -> "EL2HLT"
    EBADE           -> "EBADE"
    EBADR           -> "EBADR"
    EXFULL          -> "EXFULL"
    ENOANO          -> "ENOANO"
    EBADRQC         -> "EBADRQC"
    EBADSLT         -> "EBADSLT"
    EDEADLOCK       -> "EDEADLOCK"
    EBFONT          -> "EBFONT"
    ENOSTR          -> "ENOSTR"
    ENODATA         -> "ENODATA"
    ETIME           -> "ETIME"
    ENOSR           -> "ENOSR"
    ENONET          -> "ENONET"
    ENOPKG          -> "ENOPKG"
    EREMOTE         -> "EREMOTE"
    ENOLINK         -> "ENOLINK"
    EADV            -> "EADV"
    ESRMNT          -> "ESRMNT"
    ECOMM           -> "ECOMM"
    EPROTO          -> "EPROTO"
    EMULTIHOP       -> "EMULTIHOP"
    EDOTDOT         -> "EDOTDOT"
    EBADMSG         -> "EBADMSG"
    EOVERFLOW       -> "EOVERFLOW"
    ENOTUNIQ        -> "ENOTUNIQ"
    EBADFD          -> "EBADFD"
    EREMCHG         -> "EREMCHG"
    ELIBACC         -> "ELIBACC"
    ELIBBAD         -> "ELIBBAD"
    ELIBSCN         -> "ELIBSCN"
    ELIBMAX         -> "ELIBMAX"
    ELIBEXEC        -> "ELIBEXEC"
    EILSEQ          -> "EILSEQ"
    ERESTART        -> "ERESTART"
    ESTRPIPE        -> "ESTRPIPE"
    EUSERS          -> "EUSERS"
    ENOTSOCK        -> "ENOTSOCK"
    EDESTADDRREQ    -> "EDESTADDRREQ"
    EMSGSIZE        -> "EMSGSIZE"
    EPROTOTYPE      -> "EPROTOTYPE"
    ENOPROTOOPT     -> "ENOPROTOOPT"
    EPROTONOSUPPORT -> "EPROTONOSUPPORT"
    ESOCKTNOSUPPORT -> "ESOCKTNOSUPPORT"
    EOPNOTSUPP      -> "EOPNOTSUPP"
    EPFNOSUPPORT    -> "EPFNOSUPPORT"
    EAFNOSUPPORT    -> "EAFNOSUPPORT"
    EADDRINUSE      -> "EADDRINUSE"
    EADDRNOTAVAIL   -> "EADDRNOTAVAIL"
    ENETDOWN        -> "ENETDOWN"
    ENETUNREACH     -> "ENETUNREACH"
    ENETRESET       -> "ENETRESET"
    ECONNABORTED    -> "ECONNABORTED"
    ECONNRESET      -> "ECONNRESET"
    ENOBUFS         -> "ENOBUFS"
    EISCONN         -> "EISCONN"
    ENOTCONN        -> "ENOTCONN"
    ESHUTDOWN       -> "ESHUTDOWN"
    ETOOMANYREFS    -> "ETOOMANYREFS"
    ETIMEDOUT       -> "ETIMEDOUT"
    ECONNREFUSED    -> "ECONNREFUSED"
    EHOSTDOWN       -> "EHOSTDOWN"
    EHOSTUNREACH    -> "EHOSTUNREACH"
    EALREADY        -> "EALREADY"
    EINPROGRESS     -> "EINPROGRESS"
    ESTALE          -> "ESTALE"
    EUCLEAN         -> "EUCLEAN"
    ENOTNAM         -> "ENOTNAM"
    ENAVAIL         -> "ENAVAIL"
    EISNAM          -> "EISNAM"
    EREMOTEIO       -> "EREMOTEIO"
    EDQUOT          -> "EDQUOT"
    ENOMEDIUM       -> "ENOMEDIUM"
    EMEDIUMTYPE     -> "EMEDIUMTYPE"
    ECANCELED       -> "ECANCELED"
    ENOKEY          -> "ENOKEY"
    EKEYEXPIRED     -> "EKEYEXPIRED"
    EKEYREVOKED     -> "EKEYREVOKED"
    EKEYREJECTED    -> "EKEYREJECTED"
    EOWNERDEAD      -> "EOWNERDEAD"
    ENOTRECOVERABLE -> "ENOTRECOVERABLE"
    ERFKILL         -> "ERFKILL"
    EHWPOISON       -> "EHWPOISON"
    ENOTSUP         -> "ENOTSUP"
    else            -> null
  }

  @Suppress("DUPLICATE_LABEL_IN_WHEN")
  fun strerror(errno: Int) = when (errno) {
    EPERM           -> "Operation not permitted"
    ENOENT          -> "No such file or directory"
    ESRCH           -> "No such process"
    EINTR           -> "Interrupted system call"
    EIO             -> "Input/output error"
    ENXIO           -> "No such device or address"
    E2BIG           -> "Argument list too long"
    ENOEXEC         -> "Exec format error"
    EBADF           -> "Bad file descriptor"
    ECHILD          -> "No child processes"
    EAGAIN          -> "Resource temporarily unavailable"
    ENOMEM          -> "Cannot allocate memory"
    EACCES          -> "Permission denied"
    EFAULT          -> "Bad address"
    ENOTBLK         -> "Block device required"
    EBUSY           -> "Device or resource busy"
    EEXIST          -> "File exists"
    EXDEV           -> "Invalid cross-device link"
    ENODEV          -> "No such device"
    ENOTDIR         -> "Not a directory"
    EISDIR          -> "Is a directory"
    EINVAL          -> "Invalid argument"
    ENFILE          -> "Too many open files in system"
    EMFILE          -> "Too many open files"
    ENOTTY          -> "Inappropriate ioctl for device"
    ETXTBSY         -> "Text file busy"
    EFBIG           -> "File too large"
    ENOSPC          -> "No space left on device"
    ESPIPE          -> "Illegal seek"
    EROFS           -> "Read-only file system"
    EMLINK          -> "Too many links"
    EPIPE           -> "Broken pipe"
    EDOM            -> "Numerical argument out of domain"
    ERANGE          -> "Numerical result out of range"
    EDEADLK         -> "Resource deadlock avoided"
    ENAMETOOLONG    -> "File name too long"
    ENOLCK          -> "No locks available"
    ENOSYS          -> "Function not implemented"
    ENOTEMPTY       -> "Directory not empty"
    ELOOP           -> "Too many levels of symbolic links"
    EWOULDBLOCK     -> "Resource temporarily unavailable"
    ENOMSG          -> "No message of desired type"
    EIDRM           -> "Identifier removed"
    ECHRNG          -> "Channel number out of range"
    EL2NSYNC        -> "Level 2 not synchronized"
    EL3HLT          -> "Level 3 halted"
    EL3RST          -> "Level 3 reset"
    ELNRNG          -> "Link number out of range"
    EUNATCH         -> "Protocol driver not attached"
    ENOCSI          -> "No CSI structure available"
    EL2HLT          -> "Level 2 halted"
    EBADE           -> "Invalid exchange"
    EBADR           -> "Invalid request descriptor"
    EXFULL          -> "Exchange full"
    ENOANO          -> "No anode"
    EBADRQC         -> "Invalid request code"
    EBADSLT         -> "Invalid slot"
    EDEADLOCK       -> "Resource deadlock avoided"
    EBFONT          -> "Bad font file format"
    ENOSTR          -> "Device not a stream"
    ENODATA         -> "No data available"
    ETIME           -> "Timer expired"
    ENOSR           -> "Out of streams resources"
    ENONET          -> "Machine is not on the network"
    ENOPKG          -> "Package not installed"
    EREMOTE         -> "Object is remote"
    ENOLINK         -> "Link has been severed"
    EADV            -> "Advertise error"
    ESRMNT          -> "Srmount error"
    ECOMM           -> "Communication error on send"
    EPROTO          -> "Protocol error"
    EMULTIHOP       -> "Multihop attempted"
    EDOTDOT         -> "RFS specific error"
    EBADMSG         -> "Bad message"
    EOVERFLOW       -> "Value too large for defined data type"
    ENOTUNIQ        -> "Name not unique on network"
    EBADFD          -> "File descriptor in bad state"
    EREMCHG         -> "Remote address changed"
    ELIBACC         -> "Can not access a needed shared library"
    ELIBBAD         -> "Accessing a corrupted shared library"
    ELIBSCN         -> ".lib section in a.out corrupted"
    ELIBMAX         -> "Attempting to link in too many shared libraries"
    ELIBEXEC        -> "Cannot exec a shared library directly"
    EILSEQ          -> "Invalid or incomplete multibyte or wide character"
    ERESTART        -> "Interrupted system call should be restarted"
    ESTRPIPE        -> "Streams pipe error"
    EUSERS          -> "Too many users"
    ENOTSOCK        -> "Socket operation on non-socket"
    EDESTADDRREQ    -> "Destination address required"
    EMSGSIZE        -> "Message too long"
    EPROTOTYPE      -> "Protocol wrong type for socket"
    ENOPROTOOPT     -> "Protocol not available"
    EPROTONOSUPPORT -> "Protocol not supported"
    ESOCKTNOSUPPORT -> "Socket type not supported"
    EOPNOTSUPP      -> "Operation not supported"
    EPFNOSUPPORT    -> "Protocol family not supported"
    EAFNOSUPPORT    -> "Address family not supported by protocol"
    EADDRINUSE      -> "Address already in use"
    EADDRNOTAVAIL   -> "Cannot assign requested address"
    ENETDOWN        -> "Network is down"
    ENETUNREACH     -> "Network is unreachable"
    ENETRESET       -> "Network dropped connection on reset"
    ECONNABORTED    -> "Software caused connection abort"
    ECONNRESET      -> "Connection reset by peer"
    ENOBUFS         -> "No bufferId space available"
    EISCONN         -> "Transport endpoint is already connected"
    ENOTCONN        -> "Transport endpoint is not connected"
    ESHUTDOWN       -> "Cannot send after transport endpoint shutdown"
    ETOOMANYREFS    -> "Too many references: cannot splice"
    ETIMEDOUT       -> "Connection timed out"
    ECONNREFUSED    -> "Connection refused"
    EHOSTDOWN       -> "Host is down"
    EHOSTUNREACH    -> "No route to host"
    EALREADY        -> "Operation already in progress"
    EINPROGRESS     -> "Operation now in progress"
    ESTALE          -> "Stale file handle"
    EUCLEAN         -> "Structure needs cleaning"
    ENOTNAM         -> "Not a XENIX named type file"
    ENAVAIL         -> "No XENIX semaphores available"
    EISNAM          -> "Is a named type file"
    EREMOTEIO       -> "Remote I/O error"
    EDQUOT          -> "Disk quota exceeded"
    ENOMEDIUM       -> "No medium found"
    EMEDIUMTYPE     -> "Wrong medium type"
    ECANCELED       -> "Operation canceled"
    ENOKEY          -> "Required key not available"
    EKEYEXPIRED     -> "Key has expired"
    EKEYREVOKED     -> "Key has been revoked"
    EKEYREJECTED    -> "Key was rejected by service"
    EOWNERDEAD      -> "Owner died"
    ENOTRECOVERABLE -> "State not recoverable"
    ERFKILL         -> "Operation not possible due to RF-kill"
    EHWPOISON       -> "Memory page has hardware error"
    ENOTSUP         -> "Operation not supported"
    else            -> null
  }
}
