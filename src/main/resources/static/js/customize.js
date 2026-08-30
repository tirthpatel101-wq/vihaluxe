
// ===== ELEMENTS =====

const size = document.getElementById("size");
const jar = document.getElementById("jar");
const gift = document.getElementById("gift");
const message = document.getElementById("message");

const generateMessage =
    document.getElementById("generateMessage");

const totalPrice = document.getElementById("totalPrice");
const previewPrice = document.getElementById("previewPrice");
const previewMessage = document.getElementById("previewMessage");
const previewDescription = document.getElementById("previewDescription");

const wax = document.getElementById("wax");
const fragrance = document.getElementById("fragrance");
const color = document.querySelector(".form-control-color");
const wick = document.getElementById("wick");
const label = document.getElementById("label");

const recommendAI = document.getElementById("recommendAI");

const customImage = document.getElementById("customImage");
const customImagePreview = document.getElementById("customImagePreview");
const uploadedImagePreview = document.getElementById("uploadedImagePreview");


// =============================

function updatePrice(){

    let price = 0;

    switch(size.selectedIndex){

        case 0:
            price += 600;
            break;

        case 1:
            price += 900;
            break;

        case 2:
            price += 1200;
            break;
    }

    switch(jar.selectedIndex){

        case 0:
            price += 0;
            break;

        case 1:
            price += 200;
            break;

        case 2:
            price += 350;
            break;

        case 3:
            price += 250;
            break;
    }

    if(gift.checked){

        price += 100;

    }

    totalPrice.innerText = price;

    previewPrice.innerText = price;

    document.getElementById("price").value = price;

}


// =============================

function updatePreview(){

    previewDescription.innerHTML =

        jar.options[jar.selectedIndex].text +

        "<br>" +

        fragrance.options[fragrance.selectedIndex].text +

        "<br>" +

        wax.options[wax.selectedIndex].text +

        "<br>" +

        wick.options[wick.selectedIndex].text +

        "<br>" +

        label.options[label.selectedIndex].text;

}


// =============================

message.addEventListener("keyup",()=>{

    if(message.value===""){

        previewMessage.innerHTML="Your personalized message will appear here.";

    }

    else{

        previewMessage.innerHTML=message.value;

    }

});


// =============================

size.addEventListener("change",()=>{

    updatePrice();

});


jar.addEventListener("change",()=>{

    updatePrice();

    updatePreview();

});


gift.addEventListener("change",()=>{

    updatePrice();

});


wax.addEventListener("change",updatePreview);

fragrance.addEventListener("change",updatePreview);

wick.addEventListener("change",updatePreview);

label.addEventListener("change",updatePreview);


// =============================

const overlay=document.getElementById("candleOverlay");
const preview=document.getElementById("previewImage");

color.addEventListener("input",()=>{

    overlay.style.background=color.value;

    preview.style.filter=
        "drop-shadow(0 0 25px "+color.value+")";

    preview.classList.remove("preview-animate");

    void preview.offsetWidth;

    preview.classList.add("preview-animate");

});

// =============================
// AI GENERATE MESSAGE
// =============================

generateMessage.addEventListener("click", async () => {

    generateMessage.disabled = true;
    generateMessage.innerText = "✨ Generating...";

    try {

        const response = await fetch(
            "/ai/generate-message?fragrance="
            + encodeURIComponent(fragrance.value)
            + "&labelStyle="
            + encodeURIComponent(label.value),
            {
                method: "POST"
            }
        );

        if (!response.ok) {
            throw new Error("Request failed");
        }

        const aiMessage = await response.text();

        // Put AI message inside textarea
        message.value = aiMessage;

        // Update preview
        previewMessage.innerText = aiMessage;

    }

    catch (error) {

        console.error(error);

        alert("Unable to generate message.");

    }

    finally {

        generateMessage.disabled = false;
        generateMessage.innerText = "✨ AI Generate Message";

    }

});

// =============================
// CUSTOM IMAGE PREVIEW
// =============================

customImage.addEventListener("change", () => {

    const file = customImage.files[0];

    if (!file) {

        uploadedImagePreview.style.display = "none";
        customImagePreview.src = "";

        return;

    }

    if (!file.type.startsWith("image/")) {

        alert("Please select an image file.");

        customImage.value = "";
        uploadedImagePreview.style.display = "none";

        return;

    }

    const imageURL = URL.createObjectURL(file);

    customImagePreview.src = imageURL;

    uploadedImagePreview.style.display = "block";

});

// =============================

updatePrice();

updatePreview();

preview.classList.remove("preview-animate");

void preview.offsetWidth;

preview.classList.add("preview-animate");

recommendAI.addEventListener("click", async () => {

    recommendAI.disabled = true;
    recommendAI.innerText = "🤖 Generating...";

    try {

        const response = await fetch("/ai/recommend-design", {
            method: "POST"
        });

        if (!response.ok) {
            throw new Error("HTTP Error : " + response.status);
        }

        const design = await response.json();

        console.log(design);

        // ===== Size =====
        for (let i = 0; i < size.options.length; i++) {

            if (size.options[i].value === design.size) {

                size.selectedIndex = i;
                break;

            }

        }

        // ===== Jar =====
        for (let i = 0; i < jar.options.length; i++) {

            if (jar.options[i].value === design.jar) {

                jar.selectedIndex = i;
                break;

            }

        }

        // ===== Wax =====
        for (let i = 0; i < wax.options.length; i++) {

            if (wax.options[i].value === design.wax) {

                wax.selectedIndex = i;
                break;

            }

        }

        // ===== Fragrance =====
        for (let i = 0; i < fragrance.options.length; i++) {

            if (fragrance.options[i].value === design.fragrance) {

                fragrance.selectedIndex = i;
                break;

            }

        }

        // ===== Wick =====
        for (let i = 0; i < wick.options.length; i++) {

            if (wick.options[i].value === design.wick) {

                wick.selectedIndex = i;
                break;

            }

        }

        // ===== Label =====
        for (let i = 0; i < label.options.length; i++) {

            if (label.options[i].value === design.label) {

                label.selectedIndex = i;
                break;

            }

        }

        // ===== Color =====
        color.value = design.color;
        overlay.style.background = design.color;
        preview.style.filter =
                "drop-shadow(0 0 25px " + design.color + ")";

        // ===== Message =====
        message.value = design.message;
        previewMessage.innerHTML = design.message;

        // ===== Refresh UI =====
        updatePrice();
        updatePreview();

    }

    catch (e) {

        console.error(e);
        alert("Unable to generate AI recommendation.");

    }

    finally {

        recommendAI.disabled = false;
        recommendAI.innerText = "🤖 AI Recommend Design";

    }

});