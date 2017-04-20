package harps.blackboardscraper;

/**
 * @file Module.java
 * @author Ed Harper
 * @date 28/03/2017
 *
 * Module object to hold course module data
 */

public class Module {

    private String moduleName;
    private String moduleUrl;

    /**
     * Initialises a new instance of Module
     * @param moduleName the module names
     * @param moduleUrl the modules url
     */
    public Module(String moduleName, String moduleUrl){
        this.moduleName = moduleName;
        this.moduleUrl = moduleUrl;
    }

    /**
     * Gets the module name
     * @return the modules name
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Gets the module url
     * @return the modules module url
     */
    public String getModuleUrl() {
        return moduleUrl;
    }
}
