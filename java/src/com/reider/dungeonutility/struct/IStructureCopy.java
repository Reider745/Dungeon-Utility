package com.reider.dungeonutility.struct;

import com.reider.dungeonutility.struct.prototypes.IStructurePrototype;
import com.reider.dungeonutility.api.data.BlockData;

public interface IStructureCopy {
    BlockData copyBlock(BlockData block);
    IStructurePrototype copyPrototype(IStructurePrototype prot);
}
