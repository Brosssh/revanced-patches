package app.revanced.patches.geocaching.subscription

import app.revanced.patcher.Fingerprint
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction

@Suppress("unused")
val unlockPremiumPatch = bytecodePatch(
    name = "Unlock Premium features",
    description = "WARNING: This only works via mount install (root required). Lists are still locked as they are server sided."
) {
    compatibleWith("com.groundspeak.geocaching.intro")

    execute {
        fun overrideRegister(fingerprint: Fingerprint, indexToOverride: Int) {
            with(fingerprint.method) {

                val createUserProfileIndex = indexOfFirstInstructionOrThrow {
                    opcode == Opcode.INVOKE_DIRECT_RANGE
                }

                val createUserStartingRegister =
                    getInstruction<RegisterRangeInstruction>(createUserProfileIndex).startRegister

                val registerToOverwrite = createUserStartingRegister + 1 + indexToOverride

                addInstruction(
                    createUserProfileIndex,
                    "const/16 v${registerToOverwrite}, 0x3"
                )
            }
        }

        overrideRegister(userProfileDeserializerFingerprint, 13)

        overrideRegister(ownProfileDeserializerFingerprint, 13)
    }
}