/*
 *      Kores-SourceWriter - Translates Kores Structure to Java Source <https://github.com/JonathanxD/Kores-SourceWriter>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2018 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
 *      Copyright (c) contributors
 *
 *
 *      Permission is hereby granted, free of charge, to any person obtaining a copy
 *      of this software and associated documentation files (the "Software"), to deal
 *      in the Software without restriction, including without limitation the rights
 *      to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *      copies of the Software, and to permit persons to whom the Software is
 *      furnished to do so, subject to the following conditions:
 *
 *      The above copyright notice and this permission notice shall be included in
 *      all copies or substantial portions of the Software.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *      IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *      FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *      AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *      LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *      OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *      THE SOFTWARE.
 */
package com.github.jonathanxd.kores.source.process

import com.github.jonathanxd.iutils.option.Option

/**
 * Expand the elvis expression when it cannot be translated to valid Java,
 * if false, the processor will fail when elvis cannot be expressed in Java.
 *
 * Expand Elvis is a relative new feature and may not be correct, please report any inconsistency.
 *
 *
 * Explanation:
 *
 * What is elvis expand?
 *
 * Some set of instructions can't easily translated to source code, such as (generated by EventSys
 * using BytecodeWriter):
 *
 * ```
 *    0: aload_0
 *    1: getfield      instance/MyListener
 *    4: aload_1
 *    5: checkcast     MyGenericEvent
 *    8: aload_1
 *    9: ldc           Integer
 *    11: ldc          "obj"
 *    13: invokeinterface PropertyHolder.lookup:(Class;String;)Property;
 *    18: dup
 *    19: ifnull        25
 *    22: goto          27
 *    25: pop
 *    26: return
 *    27: dup
 *    28: ifnull        39
 *    31: invokeinterface GetterProperty.getValue:()Object;
 *    36: goto          41
 *    39: pop
 *    40: return
 *    41: checkcast     Integer
 *    44: invokevirtual MyListener.listen2:(MyGenericEvent;Integer;)V
 *    47: return
 * ```
 *
 * This is something like that in Java:
 *
 * ```java
 *  MyListener listener = this.instance;
 *  MyGenericEvent myGenericEvent = (MyGenericEvent) event;
 *  Object lookup = event.lookup(Integer.class, "obj"); // Yes, Object
 *
 *  if (lookup == null) return;
 *  if (lookup == null) return;
 *  lookup = ((GetterProperty) lookup).getValue()
 *
 *  listener.listen2(myGenericEvent, (Integer) lookup);
 * }
 * ```
 *
 * This is a particular case, and is not optimized at all, look at various goto, and unnecessary
 * ifs, this could be optimized to something like this:
 *
 * ```
 *    0: aload_0
 *    1: getfield      instance/MyListener
 *    2: aload_1
 *    3: checkcast     MyGenericEvent
 *    4: aload_1
 *    5: ldc           Integer
 *    6: ldc           "obj"
 *    7: invokeinterface PropertyHolder.lookup:(Class;String;)Property;
 *    8: dup
 *    9: ifnull        13
 *    10: invokeinterface GetterProperty.getValue:()Object;
 *    11: checkcast     Integer
 *    12: invokevirtual MyListener.listen2:(MyGenericEvent;Integer;)V
 *    13: pop
 *    14: return
 * ```
 *
 * But this is not the topic, this is how elvis expand feature works, the generated code looks like
 * an elvis expression which have variable definition in expression and return in body, example:
 *
 * ```
 *  this.instance.listen2((MyGenericEvent) event, (lookup = event.lookup(Integer.class, "obj")) ==
 * null
 *        ? return
 *        : (Integer) (((GetterProperty) lookup).getValue())
 *  )
 * ```
 *
 *
 * Because it is not possible to be expressed in Java, SourceWriter will expand the expression into
 * a normal if expression before the invocation:
 *
 * ```java
 *  Object lookup = event.lookup(Integer.class, "obj");
 *  if (lookup == null) return;
 *  else lookup = ((GetterProperty) lookup).getValue();
 *
 *  this.instance.listen2((MyGenericEvent) event, (Integer) lookup)
 * ```
 *
 * This feature is enabled by default.
 *
 */
@JvmField
val EXPAND_ELVIS = Option(true)