package io.zibi.komar.broker

import io.zibi.codec.mqtt.MqttMessage
import io.zibi.codec.mqtt.MqttMessageVariableHeader
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException

/**
 * Utility static methods, like Map get with default value, or elvis operator.
 */
object Utils {

    private val LOG = LoggerFactory.getLogger(Utils::class.java)

    fun <T, K> defaultGet(map: Map<K, T>, key: K, defaultValue: T): T {
        val value = map[key]
        return value ?: defaultValue
    }

    fun messageId(msg: MqttMessage): Int {
        return (msg.variableHeader() as MqttMessageVariableHeader).messageId
    }

//    fun readBytesAndRewind(payload: ByteArray): ByteArray {
//        val payloadContent = ByteArray(payload.readableBytes())
//        val mark = payload.readerIndex()
//        payload.readBytes(payloadContent)
//        payload.readerIndex(mark)
//        return payloadContent
//    }

    fun <T, U> loadClass(
        className: String,
        intrface: Class<T>,
        constructorArgClass: Class<U>,
        props: U
    ): T? {
        val instance: T? = try {
            // check if constructor with constructor arg class parameter
            // exists
            LOG.info(
                "Invoking constructor with {} argument. ClassName={}, interfaceName={}",
                constructorArgClass.name, className, intrface.name
            )
            this.javaClass.classLoader
                ?.loadClass(className)
                ?.asSubclass(intrface)
                ?.getConstructor(constructorArgClass)
                ?.newInstance(props)
        } catch (ex: InstantiationException) {
            LOG.warn(
                "Unable to invoke constructor with {} argument. ClassName={}, interfaceName={}, cause={}, " +
                        "errorMessage={}",
                constructorArgClass.name,
                className,
                intrface.name,
                ex.cause,
                ex.message
            )
            return null
        } catch (ex: IllegalAccessException) {
            LOG.warn(
                "Unable to invoke constructor with {} argument. ClassName={}, interfaceName={}, cause={}, " +
                        "errorMessage={}",
                constructorArgClass.name,
                className,
                intrface.name,
                ex.cause,
                ex.message
            )
            return null
        } catch (ex: ClassNotFoundException) {
            LOG.warn(
                "Unable to invoke constructor with {} argument. ClassName={}, interfaceName={}, cause={}, " +
                        "errorMessage={}",
                constructorArgClass.name,
                className,
                intrface.name,
                ex.cause,
                ex.message
            )
            return null
        } catch (e: NoSuchMethodException) {
            try {
                LOG.info(
                    "Invoking default constructor. ClassName={}, interfaceName={}",
                    className,
                    intrface.name
                )
                // fallback to default constructor
                this.javaClass.classLoader
                    ?.loadClass(className)
                    ?.asSubclass(intrface)
                    ?.getDeclaredConstructor()?.newInstance()
            } catch (ex: InstantiationException) {
                LOG.error(
                    "Unable to invoke default constructor. ClassName={}, interfaceName={}, cause={}, " +
                            "errorMessage={}", className, intrface.name, ex.cause, ex.message
                )
                return null
            } catch (ex: IllegalAccessException) {
                LOG.error(
                    "Unable to invoke default constructor. ClassName={}, interfaceName={}, cause={}, " +
                            "errorMessage={}", className, intrface.name, ex.cause, ex.message
                )
                return null
            } catch (ex: ClassNotFoundException) {
                LOG.error(
                    "Unable to invoke default constructor. ClassName={}, interfaceName={}, cause={}, " +
                            "errorMessage={}", className, intrface.name, ex.cause, ex.message
                )
                return null
            } catch (ex: NoSuchMethodException) {
                LOG.error(
                    "Unable to invoke default constructor. ClassName={}, interfaceName={}, cause={}, " +
                            "errorMessage={}", className, intrface.name, ex.cause, ex.message
                )
                return null
            } catch (ex: InvocationTargetException) {
                LOG.error(
                    "Unable to invoke default constructor. ClassName={}, interfaceName={}, cause={}, " +
                            "errorMessage={}", className, intrface.name, ex.cause, ex.message
                )
                return null
            }
        } catch (e: InvocationTargetException) {
            try {
                LOG.info(
                    "Invoking default constructor. ClassName={}, interfaceName={}",
                    className,
                    intrface.name
                )
                this.javaClass.classLoader
                    ?.loadClass(className)
                    ?.asSubclass(intrface)
                    ?.getDeclaredConstructor()?.newInstance()
            } catch (ex: InstantiationException) {
                LOG.error(
                    "Unable to invoke default constructor. ClassName={}, interfaceName={}, cause={}, " +
                            "errorMessage={}", className, intrface.name, ex.cause, ex.message
                )
                return null
            } catch (ex: IllegalAccessException) {
                LOG.error(
                    "Unable to invoke default constructor. ClassName={}, interfaceName={}, cause={}, " +
                            "errorMessage={}", className, intrface.name, ex.cause, ex.message
                )
                return null
            } catch (ex: ClassNotFoundException) {
                LOG.error(
                    "Unable to invoke default constructor. ClassName={}, interfaceName={}, cause={}, " +
                            "errorMessage={}", className, intrface.name, ex.cause, ex.message
                )
                return null
            } catch (ex: NoSuchMethodException) {
                LOG.error(
                    "Unable to invoke default constructor. ClassName={}, interfaceName={}, cause={}, " +
                            "errorMessage={}", className, intrface.name, ex.cause, ex.message
                )
                return null
            } catch (ex: InvocationTargetException) {
                LOG.error(
                    "Unable to invoke default constructor. ClassName={}, interfaceName={}, cause={}, " +
                            "errorMessage={}", className, intrface.name, ex.cause, ex.message
                )
                return null
            }
        }
        return instance
    }

}
