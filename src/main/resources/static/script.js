
// JavaScript for dynamically adding NPC and Party Character sections

// Function to generate a unique ID
function generateUniqueId(baseId) {
    var count = document.querySelectorAll(`[id^=${baseId}]`).length;
    return `${baseId}_${count}`;
}

// Function to clone and append a section with unique IDs for its elements
function cloneSectionWithUniqueIds(templateId, containerId) {
    var template = document.getElementById(templateId);
    var clone = template.cloneNode(true);

    // Update IDs for all elements within the cloned section
    var elements = clone.querySelectorAll('[id]');
    elements.forEach(function(element) {
        var baseId = element.id;
        element.id = generateUniqueId(baseId);
    });

    document.getElementById(containerId).appendChild(clone);

    // TODO: Populate the dropdowns in the new section (might need to use dataLoader.js)
}

// Event listener for adding NPC section
document.getElementById('addNpcBtn').addEventListener('click', function() {
    event.preventDefault();
    cloneSectionWithUniqueIds('npcCharacter', 'npcContainer');
});

// Event listener for adding Party Character section
document.getElementById('addPartyCharacterBtn').addEventListener('click', function() {
    event.preventDefault()
    cloneSectionWithUniqueIds('partyCharacters', 'partyCharacterContainer');
});

// Assuming a function populateDropdowns(elementId, dataType) is defined in dataLoader.js
// which populates the given dropdown with data of the specified type

// Function to populate all dropdowns in a section
function populateDropdownsInSection(section) {
    var dropdowns = section.querySelectorAll('select');
    dropdowns.forEach(function(dropdown) {
        var dataType = "data/"+dropdown.id.split('_')[0]; // Extracting the data type from the id
        loadDataForDropDown(dropdown.id); // Calling the populate function
    });
}

// Modifying the cloneSectionWithUniqueIds function to call populateDropdownsInSection
function cloneSectionWithUniqueIds(templateId, containerId) {
    var template = document.getElementById(templateId);
    var clone = template.cloneNode(true);

    // Update IDs for all elements within the cloned section
    var elements = clone.querySelectorAll('[id]');
    elements.forEach(function(element) {
        var baseId = element.id;
        element.id = generateUniqueId(baseId);
    });

    document.getElementById(containerId).appendChild(clone);
    populateDropdownsInSection(clone); // Populate dropdowns in the newly added section
}



// Implementing the Generate JSON Button functionality
document.getElementById('generateJsonBtn').addEventListener('click', function() {
    event.preventDefault();
    generateJson();
});

function generateJson()
{
    if(isDailyLimitExceeded())
    {
        showLimitExceededMessage();
        
    }
    else
    {
    var formElements = document.getElementById('dndForm').elements;
    var formData = {};
    for (var i = 0; i < formElements.length; i++) {
        var element = formElements[i];
        if (element.type !== 'button') {
            formData[element.id] = element.value;
        }
    }
    console.log(JSON.stringify(formData)); // For now, we just log the JSON to the console
    // Example usage
    const jsonData = formData;
    const promptFileUrl = 'prompt/promptv9.txt';
    loadPromptFile(promptFileUrl)
    .then(promptText => {
        const outputText = replacePlaceholders(jsonData, promptText);
        //console.log(outputText);
        postTextToServer(outputText, jsonData);
    })
    .catch(error => console.error(error));
    }
    
}
function loadPromptFile(url) {
    return fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.text();
        })
        .catch(error => console.error('Error fetching the prompt file:', error));
}
function replacePlaceholders(jsonData, promptText) {
    // Function to replace placeholders in a template
    function replaceTemplate(template, data) {
        return template.replace(/\{(\w+)\}/g, (match, key) => data[key] || match);
    }

    // Function to count repeated sections
    function countSections(jsonData, prefix) {
        let count = 0;
        while (jsonData.hasOwnProperty(`${prefix}_${count + 1}`)) {
            count++;
        }
        return count;
    }

    // Split the prompt text into parts
    const playersTemplate = promptText.split("[PLAYERS]")[1].split("[/PLAYERS]")[0];
    const npcsTemplate = promptText.split("[NPC]")[1].split("[/NPC]")[0];
    const textBeforePlayers = promptText.split("[PLAYERS]")[0];
    const textAfterPlayers = promptText.split("[/PLAYERS]")[1].split("[NPC]")[0];
    const textAfterNPCs = promptText.split("[/NPC]")[1];

    // Replace placeholders outside of player and NPC sections
    let result = replaceTemplate(textBeforePlayers, jsonData);

    // Handle the first player section
    result += replaceTemplate(playersTemplate, jsonData);

    // Handle additional player sections
    const numberOfPlayers = countSections(jsonData, 'characterClass');
    for (let i = 1; i <= numberOfPlayers; i++) {
        const playerData = {};
        for (const key in jsonData) {
            if (key.endsWith(`_${i}`)) {
                playerData[key.slice(0, -2)] = jsonData[key];
            }
        }
        result += replaceTemplate(playersTemplate, playerData);
    }

    result += replaceTemplate(textAfterPlayers, jsonData);

    // Handle the first NPC section
    result += replaceTemplate(npcsTemplate, jsonData);

    // Handle additional NPC sections
    const numberOfNpcs = countSections(jsonData, 'npcType');
    for (let i = 1; i <= numberOfNpcs; i++) {
        const npcData = {};
        for (const key in jsonData) {
            if (key.endsWith(`_${i}`)) {
                npcData[key.slice(0, -2)] = jsonData[key];
            }
        }
        result += replaceTemplate(npcsTemplate, npcData);
    }

    result += replaceTemplate(textAfterNPCs, jsonData);

    return result;
}



function toggleAdvanced() {
    event.preventDefault();
    var npcSection = document.getElementById('npcCharacter');
    var partySection = document.getElementById('partyCharacters');
    var addNpcButton = document.getElementById('addNpcBtn');
    var addPartyButton = document.getElementById('addPartyCharacterBtn');

    npcSection.style.display = (npcSection.style.display === '' || npcSection.style.display === 'none') ? 'block' : 'none';
    partySection.style.display = (partySection.style.display === '' || partySection.style.display === 'none') ? 'block' : 'none';
    addNpcButton.style.display = (addNpcButton.style.display === '' || addNpcButton.style.display === 'none') ? 'block' : 'none';
    addPartyButton.style.display = (addPartyButton.style.display === '' || addPartyButton.style.display === 'none') ? 'block' : 'none';
}

function postTextToServer(text,jsonData) {
    incrementDailyCounter();
    //showLoadingIndicator();
    fetch('/process_text?title='+jsonData.campaignName, {
        method: 'POST',
        headers: {
            'Content-Type': 'text/plain'
        },
        body: text
    })
    .then(response => response.text())
    .then(data => {
        console.log('Success:', data);
        hideLoadingIndicator();
        showCampaignModal(data);
        showProcessingMessages();
        // Load and refresh the data every 15 seconds
        displayData();
        setInterval(displayData, 15000);

    })
    .catch((error) => {
        console.error('Error:', error);
    });
}

function incrementDailyCounter() {
    const today = new Date().toISOString().split('T')[0]; // Format: YYYY-MM-DD
    const cookieName = `dailyCounter_${today}`;
    const currentCount = parseInt(getCookie(cookieName)) || 0;
    const newCount = currentCount + 1;
    
    setCookie(cookieName, newCount, 1); // Expires after 1 day
}

function setCookie(name, value, days) {
    let expires = "";
    if (days) {
        const date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "")  + expires + "; path=/";
}

function getCookie(name) {
    const nameEQ = name + "=";
    const ca = document.cookie.split(';');
    for(let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}
function isDailyLimitExceeded() {
    const today = new Date().toISOString().split('T')[0];
    const cookieName = `dailyCounter_${today}`;
    const currentCount = parseInt(getCookie(cookieName)) || 0;

    return currentCount > 5;
}
function showLimitExceededMessage() {
    document.getElementById('myModal').style.display = 'block';
}
const loadingMessages = [
    "Terraforming world", "Chasing Orcs", "Slaying Dragons", 
    "Looking for lost d12", "Summoning the Dungeon Master", 
    "Rolling initiative", "Calculating loot", "Drawing dungeon maps",
    "Choosing the perfect soundtrack", "Consulting the rulebook",
    "Painting miniatures", "Preparing epic monologues",
    "Creating plot twists", "Rolling for charisma", "Forging legendary weapons",
    "Conjuring mystical spells", "Gathering the party", "Setting up traps",
    "Deciphering ancient texts", "Bargaining with shopkeepers",
    "Finding a way out of the maze", "Escaping the dragon's lair", 
    "Recruiting allies", "Negotiating with goblins", "Brewing healing potions",
    "Aligning the stars", "Calibrating mana levels", "Activating stealth mode",
    "Consulting the oracle", "Enchanting weapons", 
    "Training pet dragons", "Sharpening swords", "Stitching wizard robes",
    "Taming wild unicorns", "Crafting magical amulets", "Reading ancient scrolls",
    "Studying spellbooks", "Polishing armor", "Scouting the enemy territory",
    "Preparing invisibility cloaks", "Fortifying defenses", "Casting protective wards",
    "Summoning spirit animals", "Chanting incantations", "Brewing storm clouds",
    "Decoding secret messages", "Rallying the troops", "Planning the banquet",
    "Cursing enemies", "Weaving dreams", "Consulting the stars",
    "Predicting the future", "Inventing new potions", "Exploring forgotten realms",
    "Mapping the astral plane", "Avoiding traps", "Setting up campfires",
    "Lighting torches", "Assembling the council", "Hiring mercenaries",
    "Negotiating treaties", "Writing heroic tales", "Crafting epic legends",
    "Planning surprise attacks", "Building secret passages", "Reading ancient runes"
];


function showProcessingMessages() {
    let messageIndex = 0;
    const messageDiv = document.querySelector('.loading-message');
    //const loadingModal = document.getElementById('loadingModal');

    //loadingModal.style.display = 'flex';
    const messages = shuffleArray([...loadingMessages]);
    const messageInterval = setInterval(() => {
        if (messageIndex >= messages.length) {
            messageIndex = 0;
        }

        messageDiv.textContent = messages[messageIndex++];
    }, 3000); // Change message every 2 seconds

    // Store the interval ID so it can be cleared later
    messageDiv.dataset.intervalId = messageInterval;
}

function hideLoadingIndicator() {
    const loadingModal = document.getElementById('loadingModal');
    clearInterval(loadingModal.dataset.intervalId); // Clear the message cycling
    loadingModal.style.display = 'none';
}
function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        // Generate a random index from 0 to i
        const j = Math.floor(Math.random() * (i + 1));

        // Swap elements at indices i and j
        [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
}
function showCampaignModal(message) {
    document.getElementById('loadingModal').text = message;
    document.getElementById('campaignReadyModal').style.display = 'flex';
}

function hideCampaignModal() {
    document.getElementById('campaignReadyModal').style.display = 'none';
}
async function fetchData(url) {
            try {
                let response = await fetch(url);
                return await response.json();
            } catch (error) {
                console.error('Error fetching data:', error);
                return [];
            }
        }

        async function displayData() {
            // Clear existing data
            document.getElementById('pdfFiles').innerHTML = '';
            document.getElementById('queueRequests').innerHTML = '';

            // Fetch and display data from /pdf-files
            const pdfFiles = await fetchData('/pdf-files');
            const pdfList = document.getElementById('pdfFiles');
            pdfFiles.slice(0, 10).forEach(file => {
                let listItem = document.createElement('li');
               
                var a = document.createElement('a');
                a.href = "/pdf/"+file;
                a.textContent = file;

                listItem.appendChild(a);
                pdfList.appendChild(listItem);
            });

            // Fetch and display data from /queue/requests
            const queueRequests = await fetchData('/queue/requests');
            const queueList = document.getElementById('queueRequests');
            queueRequests.forEach(request => {
                let listItem = document.createElement('li');
                listItem.textContent = request.title;
                queueList.appendChild(listItem);
            });
        }
