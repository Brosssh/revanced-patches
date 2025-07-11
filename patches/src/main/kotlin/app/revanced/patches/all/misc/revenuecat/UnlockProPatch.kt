package app.revanced.patches.all.misc.revenuecat

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21t
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.StringReference

@Suppress("unused")
val unlockProPatch = bytecodePatch(
    name = "Unlock RevenueCat Pro",
) {
    execute {
        proFingerprint.method.apply {
            val subscriptionsInstruction = implementation!!.instructions.first { it ->
                if (it.opcode != Opcode.CONST_STRING) return@first false

                ((it as ReferenceInstruction).reference as StringReference).string == "subscriptions"
            }

            val entitlementsInstruction = implementation!!.instructions.first { it ->
                if (it.opcode != Opcode.CONST_STRING) return@first false

                ((it as ReferenceInstruction).reference as StringReference).string == "entitlements"
            }

            addInstructions(
                subscriptionsInstruction.location.index + 3,
                """
                    const-string v4, "{\"pro_access2\":{\"auto_resume_date\":null,\"billing_issues_detected_at\":null,\"expires_date\":\"2099-12-31T00:00:00Z\",\"grace_period_expires_date\":null,\"is_sandbox\":false,\"original_purchase_date\":\"2000-01-01T00:00:00Z\",\"period_type\":\"normal\",\"product_plan_identifier\":\"monthly-plan2\",\"purchase_date\":\"2000-01-01T00:00:00Z\",\"refunded_at\":null,\"store\":\"play_store\",\"unsubscribe_detected_at\":null}}"
                    new-instance v6, Lorg/json/JSONObject;
                    invoke-direct {v6, v4}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V
                    const-string v4, "subscriptions"
                """
            )

            addInstructions(
                entitlementsInstruction.location.index + 3,
                """
                    const-string v9, "{\"Pro\":{\"expires_date\":\"2099-12-31T00:00:00Z\",\"grace_period_expires_date\":null,\"product_identifier\":\"pro_access2\",\"product_plan_identifier\":\"monthly-plan2\",\"purchase_date\":\"2000-01-01T00:00:00Z\"}}"
                    new-instance v8, Lorg/json/JSONObject;
                    invoke-direct {v8, v9}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V
                """
            )
        }

        //The above patch is for some reason not being stored in cache
        //I'm therefore forcing the app to NOT get cached values but to get them from the API instead
        //The method that get from the API is the one patched above
        infoCacheOverwritePatch.method.apply {
            val instruction = implementation!!.instructions.first {
                Opcode.IF_EQZ == it.opcode
            }
            val register =  (instruction as BuilderInstruction21t).registerA
            addInstructions(
                instruction.location.index,
                "const/4 v$register, 0x0"
            )
        }
    }
}