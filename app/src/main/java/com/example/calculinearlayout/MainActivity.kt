package com.example.calculinearlayout

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calculinearlayout.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var current: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.apply {
            a0.setOnClickListener { append("0") }
            a1.setOnClickListener { append("1") }
            a2.setOnClickListener { append("2") }
            a3.setOnClickListener { append("3") }
            a4.setOnClickListener { append("4") }
            a5.setOnClickListener { append("5") }
            a6.setOnClickListener { append("6") }
            a7.setOnClickListener { append("7") }
            a8.setOnClickListener { append("8") }
            a9.setOnClickListener { append("9") }

            plus.setOnClickListener { append("+") }
            mines.setOnClickListener { append("-") }
            kali.setOnClickListener { append("*") }
            per.setOnClickListener { append("/") }

            equal.setOnClickListener { calculate() }
            C.setOnClickListener { clear() }
            delete.setOnClickListener { deleteChar() }
        }
    }

    private fun append(value: String) {
        current += value
        binding.operation.text = current
    }

    private fun clear() {
        current = ""
        binding.operation.text = "0"
        binding.result.text = ""
    }

    private fun deleteChar() {
        if (current.isNotEmpty()) {
            current = current.dropLast(1)
            binding.operation.text = current
        }
        else {
            binding.operation.text = "0"
        }
    }

    private fun calculate() {
        try {
            val result = evaluateExpression(current)
            binding.result.text = result.toString()
            Toast.makeText(this, "Hasil : " + result, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            binding.result.text = "Error"
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun evaluateExpression(expression: String): Double {
        val tokens = expression.toCharArray()
        val values = Stack<Double>()
        val operators = Stack<Char>()

        var i = 0
        while (i < tokens.size) {
            if (tokens[i].isWhitespace()) {
                i++
                continue
            }

            if (tokens[i].isDigit()) {
                val temp = StringBuilder()
                while (i < tokens.size && (tokens[i].isDigit() || tokens[i] == '.')) {
                    temp.append(tokens[i++])
                }
                values.push(temp.toString().toDouble())
                i--
            } else if (tokens[i] == '(') {
                operators.push(tokens[i])
            } else if (tokens[i] == ')') {
                while (operators.peek() != '(') {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()))
                }
                operators.pop()
            } else if (isOperator(tokens[i])) {
                while (operators.isNotEmpty() && priority(tokens[i], operators.peek())) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()))
                }
                operators.push(tokens[i])
            }
            i++
        }

        while (operators.isNotEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()))
        }

        return values.pop()
    }

    private fun isOperator(ch: Char): Boolean {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/'
    }

    private fun priority(op1: Char, op2: Char): Boolean {
        if (op2 == '(' || op2 == ')') return false
        return !(op1 == '*' || op1 == '/') || (op2 != '+' && op2 != '-')
    }

    private fun applyOperator(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> a / b
            else -> throw UnsupportedOperationException("Operator tidak didukung: $op")
        }
    }
}
