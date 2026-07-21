package studio.lrxmc.trfp.gun;

/**
 * 枪械附件槽类型。
 * 注意：Magazine 在原 mod 中是枪械的内置槽，由 NBT 中的 DummyAmmo/Ammo 字段表达；
 * 不属于可拆卸附件，因此不在此枚举内。
 */
public enum AttachmentSlotType {
    SCOPE,    // 瞄具
    GRIP,     // 握把
    STOCK,    // 枪托
    BARREL,   // 枪管
    MUZZLE    // 枪口（与枪管类似，原 mod 用此表示消音器/补偿器）
}
