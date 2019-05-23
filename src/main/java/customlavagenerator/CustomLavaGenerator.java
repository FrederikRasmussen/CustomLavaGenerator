package customlavagenerator;

import customlavagenerator.events.LavaFlowEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
  modid = CustomLavaGenerator.MOD_ID,
  name = CustomLavaGenerator.NAME,
  version = CustomLavaGenerator.VERSION
)
public class CustomLavaGenerator {
  public static final String MOD_ID = "customlavagenerator";
  public static final String NAME = "Custom Lava Generator";
  public static final String VERSION = "@VERSION@";

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    Configuration.updateConfig();
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
      MinecraftForge.EVENT_BUS.register(new LavaFlowEvents());
  }
}