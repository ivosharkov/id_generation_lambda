package com.scalefocus.shutterfly.aws.idgeneration.dao.impl;

import com.scalefocus.shutterfly.aws.idgeneration.dao.api.INodeIdsDao;

import java.security.SecureRandom;

/**
 * Simple ID-assigner that generates random IDs.
 * Instantiated with the 'test' profile automatically.
 *
 * @author nikola
 */
public class NodeIdsRandom implements INodeIdsDao {

    public Byte getId() {
        SecureRandom secureRandom = new SecureRandom();
        return (byte) secureRandom.nextInt(256);
    }
}