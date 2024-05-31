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
                form(
                    action = "/message",
                    method = FormMethod.post,
                    encType = FormEncType.multipartFormData,
                ) {
                    target = "_blank"
                    autoComplete = false
                    messageInput()
                    options()
                    justification()
                    fontSize("font_height", "Font Height")
                    fontSize("font_width", "Font Width")
                }
                api()
            }
        })
}

fun FlowContent.messageInput() {
    div(classes = "message_input") {
        h3 { +"Message" }
        textArea(cols = "50") {
            required = true
            name = "message"
            placeholder = "Enter your message here"
        }
        h6 {
            +"Image to be printed "
            select {
                name="image_position"
                option {
                    value = "above"
                    +"above"
                }
                option {
                    value = "below"
                    +"below"
                }
            }
            +" the text"
        }
        fileInput {
            id = "image"
            name = "image"
            accept = "image/*"
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
        checkboxRow("white_on_black", "White on Black")
    }
}

fun FlowContent.checkboxRow(name: String, label: String) {
    div(classes = "checkbox_row") {
        checkBoxInput(name = name) {
            id = name
            value = "true"
        }
        label {
            id = "$name-label"
            htmlFor = name
            +label
        }
    }
}

fun FlowContent.fontSize(name: String, label: String) {
    div(classes = "justification") {
        h3 {
            +label
        }
        select {
            id = name
            this.name = name
            1.rangeTo(8).forEach {
                option {
                    value = it.toString()
                    +it.toString()
                }
            }
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
    }
}

fun FlowContent.radioButtons(name: String, vararg values: Pair<String, String>) {
    values.forEachIndexed { index, (value, label) ->
        radioRow(name, value, label, index == 0)
    }
}

fun FlowContent.radioRow(name: String, value: String, label: String, checked: Boolean = false) {
    div(classes = "radio_row") {
        radioInput(name = name) {
            if (checked) {
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

fun FlowContent.api() {
    div(classes = "api") {
        h3 { +"API" }
        a(href = "/assets/openapi.yaml") {
            +"OpenAPI Specification"
        }
        p {
            +"POST /message"
        }
        p {
            +"Content-type: application/x-www-form-urlencoded"
        }
        p {
            +"Parameters:"
        }
        pre {
            code {
                +"message: required\n"
                +"bold: true|false, default: false\n"
                +"underline: true|false, default: false\n"
                +"justification: left|right|center, default: left\n"
                +"font_width: 1-8, default: 1\n"
                +"font_height: 1-8, default: 1\n"
            }
        }
    }
}