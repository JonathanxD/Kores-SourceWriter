/*
 *      CodeAPI-SourceWriter - Framework to generate Java code and Bytecode code. <https://github.com/JonathanxD/CodeAPI-SourceWriter>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2017 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/ & https://github.com/TheRealBuggy/) <jonathan.scripter@programmer.net>
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
package com.github.jonathanxd.codeapi.source.test;

import com.github.jonathanxd.codeapi.base.TypeDeclaration;
import com.github.jonathanxd.codeapi.source.process.JavaSourceGeneratorOptionsKt;
import com.github.jonathanxd.codeapi.test.FakeElvisTest_;

import org.junit.Test;

/**
 * Tests the elvis expand feature added in 4.0.
 *
 * What is elvis expand?
 *
 * Some set of instructions can't easily translated to source code, such as (generated by EventSys
 * using BytecodeWriter):
 *
 * <pre>
 * {@code
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
 * }
 * </pre>
 *
 * This is something like that in Java:
 *
 * <pre>
 * {@code
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
 * </pre>
 *
 * This is a particular case, and is not optimized at all, look at various goto, and unnecessary
 * ifs, this could be optimized to something like this:
 *
 * <pre>
 * {@code
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
 * }
 * </pre>
 *
 * But this is not the topic, this is how elvis expand feature works, the generate code looks like
 * an elvis expression which have variable definition in expression and return in body:
 *
 * <pre>
 * {@code
 *  this.instance.listen2((MyGenericEvent) event, (lookup = event.lookup(Integer.class, "obj")) ==
 * null
 *        ? return
 *        : (Integer) (((GetterProperty) lookup).getValue())
 *  )
 * }
 * </pre>
 *
 *
 * Because it is not possible to be expressed in Java, SourceWriter will expand the expression into
 * a normal if expression before the invocation:
 *
 * <pre>
 * {@code
 *  Object lookup = event.lookup(Integer.class, "obj");
 *  if (lookup == null) return;
 *  else lookup = ((GetterProperty) lookup).getValue();
 *
 *  this.instance.listen2((MyGenericEvent) event, (Integer) lookup)
 * }
 * </pre>
 *
 * This feature is enabled by default.
 */
public class ElvisExpandTest {

    @Test
    public void elvisExpandTest() {
        TypeDeclaration test = FakeElvisTest_.$();
        SourceTest sourceTest = CommonSourceTest.test(ElvisExpandTest.class, test);

        sourceTest.expect("package com;\n" +
                "\n" +
                "import com.github.jonathanxd.codeapi.test.FakeElvisTest_.TestClass;\n" +
                "\n" +
                "public class FakeElvisTest {\n" +
                "\n" +
                "    public TestClass test(String a) {\n" +
                "        String stack_var$ = null;\n" +
                "        if (a == null) {\n" +
                "            TestClass.noti();\n" +
                "            stack_var$ = \"\";\n" +
                "        } else {\n" +
                "            TestClass.noti2();\n" +
                "            stack_var$ = a;\n" +
                "        }\n" +
                "        return new TestClass(stack_var$);\n" +
                "    }\n" +
                "}\n");
    }

}
