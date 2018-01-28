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
package com.github.jonathanxd.kores.source.process.processors

import com.github.jonathanxd.iutils.data.TypedData
import com.github.jonathanxd.kores.base.Annotable
import com.github.jonathanxd.kores.base.FieldDeclaration
import com.github.jonathanxd.kores.base.ModifiersHolder
import com.github.jonathanxd.kores.base.comment.CommentHolder
import com.github.jonathanxd.kores.common.KoresNothing
import com.github.jonathanxd.kores.processor.ProcessorManager
import com.github.jonathanxd.kores.processor.processAs
import com.github.jonathanxd.kores.safeForComparison
import com.github.jonathanxd.kores.source.process.AppendingProcessor

object FieldDeclarationProcessor :
    AppendingProcessor<FieldDeclaration> {

    override fun process(
        part: FieldDeclaration,
        data: TypedData,
        processorManager: ProcessorManager<*>,
        appender: com.github.jonathanxd.kores.source.process.JavaSourceAppender
    ) {
        processorManager.processAs<CommentHolder>(part, data)
        processorManager.processAs<Annotable>(part, data)
        processorManager.processAs<ModifiersHolder>(part, data)

        processorManager.processAs(part.type, data)
        appender += " "
        appender += part.name

        if (part.value.safeForComparison != KoresNothing) {
            appender += " = "
            processorManager.processAs(part.value, data)
        }

        appender += ";"

    }

}
