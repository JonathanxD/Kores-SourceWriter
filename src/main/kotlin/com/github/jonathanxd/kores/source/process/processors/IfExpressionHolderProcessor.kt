/*
 *      CodeAPI-SourceWriter - Translates CodeAPI Structure to Java Source <https://github.com/JonathanxD/CodeAPI-SourceWriter>
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
import com.github.jonathanxd.kores.base.IfExpr
import com.github.jonathanxd.kores.base.IfExpressionHolder
import com.github.jonathanxd.kores.base.IfGroup
import com.github.jonathanxd.kores.operator.Operator
import com.github.jonathanxd.kores.processor.ProcessorManager
import com.github.jonathanxd.kores.processor.processAs
import com.github.jonathanxd.kores.safeForComparison
import com.github.jonathanxd.kores.source.process.AppendingProcessor

object IfExpressionHolderProcessor :
    AppendingProcessor<IfExpressionHolder> {

    override fun process(
        part: IfExpressionHolder,
        data: TypedData,
        processorManager: ProcessorManager<*>,
        appender: com.github.jonathanxd.kores.source.process.JavaSourceAppender
    ) {
        appender += "("

        val expressions = part.expressions

        expressions.forEach { codePart ->
            val safe = codePart.safeForComparison
            when (safe) {
                is IfExpr -> processorManager.processAs(
                    codePart,
                    data
                ) // No problem, no safe cast expected
                is Operator -> appender += " ${safe.name} "
                is IfGroup -> processorManager.processAs(codePart, data) // No problem
                else -> appender.simpleAppend("/* Skipped $safe in if translation */")
            }
        }


        appender += ")"

    }

}