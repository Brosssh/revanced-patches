package app.revanced.patches.komoot.premium

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val premiumConfigFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR)
    opcodes(
        Opcode.MOVE_OBJECT,
        Opcode.INVOKE_DIRECT
    )
    custom { _, classDef ->
        classDef.endsWith("api/model/AppConfigV3;")
    }
}