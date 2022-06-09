package com.reider.dungeonutility.struct;

import com.reider.dungeonutility.api.StructurePrototypeInterface;
import com.reider.dungeonutility.api.data.BlockData;

public interface StructureCopy {
    BlockData copyBlock(BlockData block);
    StructurePrototypeInterface copyPrototype(StructurePrototypeInterface prot);
}
