package org.example;

import java.util.HashSet;

/**
 * Handles setting the properties for omitting nodes or keeping them as leafs.
 *
 * @author themis
 */
public class ASTExtractorProperties {

    /**
     * The nodes of the AST that should be printed as they are.
     */
    public static HashSet<String> LEAF = new HashSet<String>();

    /**
     * The nodes of the AST that should be omitted.
     */
    public static HashSet<String> OMIT = new HashSet<String>();

}