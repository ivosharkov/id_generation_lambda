package com.scalefocus.shutterfly.aws.idgeneration.services.api;

/**
 * Service to generate unique IDs.
 */
public interface IUniqueIdService {
	/**
	 * The main method of this service.
	 * @return Returns a unique identification number
	 */
	Long getUniqueId();

}
