const chatBox=document.getElementById("aiChatBox");

document
.getElementById("openAiChat")
.onclick=()=>{

    chatBox.style.display="flex";

};

document
.getElementById("closeAiChat")
.onclick=()=>{

    chatBox.style.display="none";

};

document
.getElementById("sendMessage")
.onclick=()=>{

    const text=document
    .getElementById("chatPrompt")
    .value;

    if(text==="")
        return;

    const messages=document
    .getElementById("chatMessages");

    messages.innerHTML+=
    `
    <div class="user-message">

        ${text}

    </div>
    `;

    document
    .getElementById("chatPrompt")
    .value="";

    messages.scrollTop=messages.scrollHeight;
};