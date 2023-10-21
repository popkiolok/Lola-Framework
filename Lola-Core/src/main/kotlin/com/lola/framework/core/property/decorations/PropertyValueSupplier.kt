package com.lola.framework.core.property.decorations

import com.lola.framework.core.decoration.ValueSupplier
import com.lola.framework.core.property.Property
import com.lola.framework.core.property.PropertyDecoration

/**
 * This interface represents a property initializer and extends the PropertyDecoration interface.
 */
interface PropertyValueSupplier : PropertyDecoration, ValueSupplier<Property, Any?>