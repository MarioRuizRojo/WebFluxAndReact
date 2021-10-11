package com.springboot.webflux.restapi.app;

import java.util.UUID;

/**
 * 
 *  @author Mario Ruiz Rojo
 * 	<br/>
 * 	Generic functions class
 * 	<br/>
 *  Functions to generate valid names for file system
 */
public class Utils {
	
	/**
	 * It returns the name in formatlessName formated to fit the file system format
	 * @param formatlessName
	 * @return formatedFileName
	 */
	private static String fileFormatName(String formatlessName) {
		return formatlessName.replace(" ", "-") //no spaces
		.replace(":", "")//no colon
		.replace("\\", "");//no back slash
	}
	
	/**
	 * Generates random id and concats it to the file name
	 * @param notUniqueName
	 * @return unique file name
	 */
	public static String generateUniqueName(String notUniqueName) {
		return UUID.randomUUID().toString()+"-"+fileFormatName(notUniqueName);
	}
}
