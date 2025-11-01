package io.github.elias.forestryextras;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Основной класс мода ForestryExtras
 */
@Mod(ForestryExtras.MOD_ID)
public class ForestryExtras {
    
    public static final String MOD_ID = "forestryextras";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    
    public ForestryExtras() {
        LOGGER.info("ForestryExtras mod initialized!");
    }
}
