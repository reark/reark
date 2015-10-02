package com.tehmou.rxbookapp.data.schematicProvider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by ttuo on 14/07/15.
 */
public interface JsonIdColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey String ID = "id";
    @DataType(DataType.Type.TEXT) String JSON = "json";
}
