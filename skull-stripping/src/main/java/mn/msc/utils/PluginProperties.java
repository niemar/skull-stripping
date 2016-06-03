package mn.msc.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author niemar
 *
 */
public class PluginProperties {
	private Class<?> clazz;
	private String outputDir;
	private String args;
	private String columnDisplayedName;
	private boolean duplicate;
	
	public PluginProperties(Class<?> clazz, String outputDir, String args, String columnDisplayedName, boolean duplicate) {
		this.clazz = clazz;
		this.outputDir = outputDir;
		this.args = args;
		this.columnDisplayedName = columnDisplayedName;
		this.duplicate = duplicate;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	public String getColumnDisplayedName() {
		return columnDisplayedName;
	}

	public void setColumnDisplayedName(String columnDisplayedName) {
		this.columnDisplayedName = columnDisplayedName;
	}
	
	public static List<String> getDirs(List<PluginProperties> plugins) {
		if(plugins == null || plugins.isEmpty())
			return Collections.emptyList();
		List<String> dirs = new ArrayList<>();
		for (PluginProperties pluginProperties : plugins) {
			dirs.add(pluginProperties.getOutputDir());
		}
		return dirs;
	}

	public static List<String> getColumnsDisplayedNames(List<PluginProperties> plugins) {
		if(plugins == null || plugins.isEmpty())
			return Collections.emptyList();
		List<String> columnsDisplayedNames = new ArrayList<>();
		for (PluginProperties pluginProperties : plugins) {
			columnsDisplayedNames.add(pluginProperties.getColumnDisplayedName());
		}
		return columnsDisplayedNames;
	}

	/**
	 * @return
	 */
	public boolean duplicateImage() {
		return duplicate;
	}
}
