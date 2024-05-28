package de.chasenet

import io.javalin.http.Context
import kotlinx.html.*
import kotlinx.html.stream.createHTML

fun Context.sendHttp() {
    this.html(
    createHTML().html {
        lang = "en"
        head {
            meta(charset = "UTF-8")
            title("POS Printer")
            link(rel = "stylesheet", href = "/assets/style.css")
        }
        body {
            form(action = "/message", method = FormMethod.post) {
                autoComplete = false
                messageInput()
                options()
                justification()
            }
        }
    })
}

fun FlowContent.messageInput() {
    div(classes = "message_input") {
        h3 { +"Message" }
        textArea(cols = "50") {
            name = "message"
            placeholder = "Enter your message here"
        }
        button(type = ButtonType.submit) {
            id = "submit"
            +"Print"
        }
    }
}

fun FlowContent.options() {
    div(classes = "options") {
        h3 { +"Options" }
        checkboxRow("bold", "Bold")
        checkboxRow("underline", "Underline")
    }
}

fun FlowContent.checkboxRow(name: String, label: String) {
    div(classes = "checkbox_row") {
        checkBoxInput(name=name) {
            id = name
        }
        label {
            htmlFor = name
            +label
        }
    }
}

fun FlowContent.justification() {
    div(classes = "justification") {
        h3 { +"Justification" }
        radioButtons(
            "justification",
            "left" to "Left",
            "center" to "Center",
            "right" to "Right"
        )
        radioRow("justification", "left", "Left", true)
        radioRow("justification", "center", "Center")
        radioRow("justification", "right", "Right")
    }
}

fun FlowContent.radioButtons(name: String, vararg values: Pair<String,String>) {
    values.forEachIndexed { index, (label, value) ->
        radioRow(name, value, label, index == 0)
    }
}

fun FlowContent.radioRow(name: String, value: String, label: String, checked: Boolean = false) {
    div(classes = "radio_row") {
        radioInput(name=name) {
            if(checked) {
                attributes["checked"] = "checked"
            }
            this.value = value
            id = name
        }
        label {
            htmlFor = value
            +label
        }
    }
}