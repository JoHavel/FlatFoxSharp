package index

import app.*
import kotlinx.browser.window
import react.create
import react.dom.client.createRoot
import web.dom.document

fun main() {
//    requireAll(require.context("src", true, js("/\\.css$/") as RegExp))
    window.onbeforeunload = {
        it.preventDefault()
        "Saved?"
    }
    document.getElementById("root")?.let { createRoot(it).render(App.create()) }
}
