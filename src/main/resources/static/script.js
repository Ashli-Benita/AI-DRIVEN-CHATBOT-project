function escapeHtml(text) {
    return text
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;");
}

function addMessage(text, className) {
    const div = document.createElement("div");
    div.className = className;
    div.innerHTML = text;

    const box = document.getElementById("chatBox");
    box.appendChild(div);
    box.scrollTop = box.scrollHeight;

    return div;
}

function speakText(text) {

    const speech = new SpeechSynthesisUtterance(text);
    speech.lang = "en-US";
    speech.rate = 1;
    speech.pitch = 1;

    window.speechSynthesis.speak(speech);
}

// 🎤 VOICE INPUT
function startVoice() {

    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;

    if (!SpeechRecognition) {
        alert("Voice not supported in this browser");
        return;
    }

    const recognition = new SpeechRecognition();
    recognition.lang = "en-US";
    recognition.interimResults = false;

    recognition.start();

    recognition.onresult = function(event) {
        const text = event.results[0][0].transcript;
        document.getElementById("message").value = text;
        sendMessage();
    };
}

// 💬 SEND MESSAGE (STREAMING)
function sendMessage() {

    let input = document.getElementById("message");
    let msg = input.value.trim();

    if (msg === "") return;

    addMessage(escapeHtml(msg), "user");
    input.value = "";

    let botDiv = addMessage("...", "bot");

    fetch("/chat?message=" + encodeURIComponent(msg))
        .then(response => {

            const reader = response.body.getReader();
            const decoder = new TextDecoder();

            let fullText = "";

            function read() {
                return reader.read().then(({ value, done }) => {

                    if (done) {
                        speakText(fullText); // 🔊 AI SPEAKS RESPONSE
                        return;
                    }

                    fullText += decoder.decode(value);
                    botDiv.innerHTML = escapeHtml(fullText);

                    document.getElementById("chatBox").scrollTop =
                        document.getElementById("chatBox").scrollHeight;

                    return read();
                });
            }

            return read();
        });
}

// 📜 LOAD HISTORY
function loadHistory() {

    fetch("/chat/history")
        .then(res => res.json())
        .then(data => {

            const box = document.getElementById("chatBox");
            box.innerHTML = "";

            data.forEach(chat => {
                addMessage(escapeHtml(chat.userMessage), "user");
                addMessage(escapeHtml(chat.botReply), "bot");
            });
        });
}

// 🧹 CLEAR CHAT
function clearChat() {

    fetch("/chat/clear", { method: "DELETE" })
        .then(() => {
            document.getElementById("chatBox").innerHTML = "";
        });
}

document.addEventListener("DOMContentLoaded", loadHistory);