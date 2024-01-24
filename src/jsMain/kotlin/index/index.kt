package index

import app.*
import react.create
import react.dom.client.createRoot
import web.dom.document

fun main() {
//    requireAll(require.context("src", true, js("/\\.css$/") as RegExp))

    document.getElementById("root")?.let { createRoot(it).render(App.create()) }
}
