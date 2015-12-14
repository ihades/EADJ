/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.web.presentation.util;

/**
 *
 * @author edm
 */
public final class EnumTranslator {
  public static String getMessageKey(Enum<?> e) {
    return e.getClass().getSimpleName() + '.' + e.name();
  }
}
