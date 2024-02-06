package com.lab.tb.distributed.chat

import com.lab.tb.distributed.base.JsonUtils
import com.lab.tb.distributed.crypto.CryptoManager
import com.lab.tb.distributed.model.PublicPart
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.validator.PublicClassValidator
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AndroidGreetingTest {

    @Test
    fun testExample() {
        assertTrue("Check Android is mentioned", Greeting().greet().contains("Android"))
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun testHello() {
        val hello = Base64.decode("WzE2MCwgNTgsIDE2MywgMjE4LCAxNzcsIDc1LCA3LCA5NSwgMTA3LCA4OCwgMTAzLCAxNjQsIDMwLCAyNDQsIDI1MiwgMjQxLCAyLCA2LCAzMiwgNzgsIDM5LCA3MCwgNzUsIDE3NiwgMzAsIDEzMiwgMTc1LCA1NCwgMTc3LCA1NSwgMjI4LCAxNTQsIDAsIDE4NCwgMjEwLCAxMTAsIDIyMiwgMjIsIDgwLCAyMjEsIDE3LCAyNTUsIDE4OCwgMjE5LCAzLCAyMiwgNjgsIDE2OCwgMTEzLCAxNDksIDU4LCA3MiwgMjA3LCAyMjcsIDI4LCAxMTcsIDI1NCwgMTQxLCA0NSwgMTkyLCAxNTMsIDYzLCAxMDksIDEzOF0=")
        val helloString = String(hello)
        val aaa = Base64.decode("BYWf8vCPXzNRETtGW/L/uz4P4M2F5vAjSdEXbfOJliMN")
        val aaaString = String(aaa)
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun hello() {

        val publicpart = """{"signedPreKeyPublicKey":"BZPhkf4qQ46N4s2iojrg1R8liqiZNqM\/X0TSJ\/S1Ia9b","signedPreKeyId":4790210,"preKeyId":12843910,"preKeyPublicKey":"BVsunn1mTiBBGVfSZ4rHbIZ+yw+huvSyaBUmOsLDPOZW","registrationId":13510,"deviceId":2,"cipherVersion":"0.0.1","signedPreKeySignature":"81pv78O\/f7nxBy2EHIIckYhd\/ndQE7mviSBeKYQixiyUnlnFiVCLFOczwTk+svO8DzkaItwqrnJ2b+Qm1uYJjA==","identityKeyPairPublicKey":"BeOews7EYAahQUa9W5MT6aNJbKDpY3jNH\/J8Cs+KSYA2","name":"44AEA8AE-DB2D-41A5-BDBD-BAF7C4CD9D36"}"""
        val resutl = JsonUtils.decodeFromString<PublicPart>(publicpart)
        println(resutl.signedPreKeyPublicKey)

        val haha = Base64.decode(resutl.signedPreKeyPublicKey!!)
        haha.forEach {
            print(it.toUByte())
            print(" ")
        }

//        val other = Base64.decode("""BZPhkf4qQ46N4s2iojrg1R8liqiZNqM\/X0TSJ\/S1Ia9b""")
//        println("")
//        other.forEach {
//            print("")
//            print(it.toUByte())
//        }
//        val haha = Base64.decode("BZPhkf4qQ46N4s2iojrg1R8liqiZNqM\/X0TSJ\/S1Ia9b")
//        "publicPart": {
//            "signedPreKeyPublicKey": "BZPhkf4qQ46N4s2iojrg1R8liqiZNqM\/X0TSJ\/S1Ia9b",
//            "signedPreKeyId": 4790210,
//            "preKeyId": 12843910,
//            "preKeyPublicKey": "BVsunn1mTiBBGVfSZ4rHbIZ+yw+huvSyaBUmOsLDPOZW",
//            "registrationId": 13510,
//            "deviceId": 2,
//            "cipherVersion": "0.0.1",
//            "signedPreKeySignature": "81pv78O\/f7nxBy2EHIIckYhd\/ndQE7mviSBeKYQixiyUnlnFiVCLFOczwTk+svO8DzkaItwqrnJ2b+Qm1uYJjA==",
//            "identityKeyPairPublicKey": "BeOews7EYAahQUa9W5MT6aNJbKDpY3jNH\/J8Cs+KSYA2",
//            "name": "44AEA8AE-DB2D-41A5-BDBD-BAF7C4CD9D36"
//        }

//            val haha = """BZPhkf4qQ46N4s2iojrg1R8liqiZNqM\/X0TSJ\/S1Ia9b"""
        }
}