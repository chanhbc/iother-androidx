package com.chanhbc.iother

import android.util.Log

@Suppress("unused", "MemberVisibilityCanBePrivate", "SameParameterValue", "ClassName")
object ILog {

    private var enabled = true
    private var MY_TAG = "bc"

    enum class SUB_TYPE {
        // normal log
        LOG,

        // change tag default(BC), tag = objects[0], log
        TAG_LOG,

        // location (this) from class, this(class) = objects[0], log
        T_LOG,

        // tag, this, log
        TAG_T_LOG
    }

    enum class LOG_TYPE {
        D, E, I, V, W, WTF
    }

    fun setEnabled(enabled: Boolean) {
        ILog.enabled = enabled
    }

    fun setTag(myTag: String) {
        MY_TAG = myTag
    }

    fun d(vararg objLog: Any?) {
        d(SUB_TYPE.LOG, *objLog)
    }

    fun d(subType: SUB_TYPE, vararg objects: Any?) {
        log(LOG_TYPE.D, subType, *objects)
    }

    fun e(vararg objLog: Any?) {
        e(SUB_TYPE.LOG, *objLog)
    }

    fun e(subType: SUB_TYPE, vararg objects: Any?) {
        log(LOG_TYPE.E, subType, *objects)
    }

    fun i(vararg objLog: Any?) {
        i(SUB_TYPE.LOG, *objLog)
    }

    fun i(subType: SUB_TYPE, vararg objects: Any?) {
        log(LOG_TYPE.I, subType, *objects)
    }

    fun v(vararg objLog: Any?) {
        v(SUB_TYPE.LOG, *objLog)
    }

    fun v(subType: SUB_TYPE, vararg objects: Any?) {
        log(LOG_TYPE.V, subType, *objects)
    }

    fun w(vararg objLog: Any?) {
        w(SUB_TYPE.LOG, *objLog)
    }

    fun w(subType: SUB_TYPE, vararg objects: Any?) {
        log(LOG_TYPE.W, subType, *objects)
    }

    fun wtf(vararg objLog: Any?) {
        wtf(SUB_TYPE.LOG, *objLog)
    }

    fun wtf(subType: SUB_TYPE, vararg objects: Any?) {
        log(LOG_TYPE.WTF, subType, *objects)
    }

    private fun log(logType: LOG_TYPE, subType: SUB_TYPE, vararg objects: Any?) {
        if (objects.isEmpty()) {
            log(logType, MY_TAG, "<no log>")
            return
        }
        when (subType) {
            SUB_TYPE.LOG -> {
                log(logType, MY_TAG, IOther.arrayToString(*objects))
            }

            SUB_TYPE.T_LOG -> {
                if (objects.size > 1) {
                    val cls = objects[0]
                    if (cls is Any) {
                        log(
                            logType,
                            MY_TAG,
                            cls.javaClass.simpleName + "-" + IOther.arrayToString(
                                *objects.copyOfRange(1, objects.size)
                            )
                        )
                    } else {
                        log(
                            logType,
                            MY_TAG,
                            null + "-" + IOther.arrayToString(
                                *objects.copyOfRange(1, objects.size)
                            )
                        )
                    }
                } else {
                    log(logType, MY_TAG, IOther.arrayToString(*objects))
                }
            }

            SUB_TYPE.TAG_LOG -> {
                if (objects.size > 1) {
                    log(
                        logType,
                        objects[0].toString(),
                        IOther.arrayToString(*objects.copyOfRange(1, objects.size))
                    )
                } else {
                    log(logType, MY_TAG, IOther.arrayToString(*objects))
                }
            }

            SUB_TYPE.TAG_T_LOG -> {
                if (objects.size > 2) {
                    val cls = objects[1]
                    if (cls is Any) {
                        log(
                            logType,
                            objects[0].toString(),
                            cls.javaClass.simpleName + "-" + IOther.arrayToString(
                                *objects.copyOfRange(2, objects.size)
                            )
                        )
                    } else {
                        log(
                            logType,
                            objects[0].toString(),
                            null + "-" + IOther.arrayToString(
                                *objects.copyOfRange(2, objects.size)
                            )
                        )
                    }
                } else {
                    log(logType, MY_TAG, IOther.arrayToString(*objects))
                }
            }
        }
    }

    private fun log(logType: LOG_TYPE, TAG: String, objLog: Any?) {
        if (!enabled) {
            return
        }
        when (logType) {
            LOG_TYPE.D -> {
                Log.d(TAG, objLog.toString())
            }

            LOG_TYPE.E -> {
                Log.e(TAG, objLog.toString())
            }

            LOG_TYPE.I -> {
                Log.i(TAG, objLog.toString())
            }

            LOG_TYPE.V -> {
                Log.v(TAG, objLog.toString())
            }

            LOG_TYPE.W -> {
                Log.w(TAG, objLog.toString())
            }

            LOG_TYPE.WTF -> {
                Log.wtf(TAG, objLog.toString())
            }
        }
    }

}
