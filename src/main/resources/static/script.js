
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
    const outputText = replacePlaceholders(jsonData, fetchStory());
    postTextToServer(outputText, jsonData);
    /*console.log(JSON.stringify(formData)); // For now, we just log the JSON to the console
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
    }*/
    
}
/*function loadPromptFile(url) {
    return fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.text();
        })
        .catch(error => console.error('Error fetching the prompt file:', error));
}*/
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
  "Terraforming world",
  "Chasing Orcs",
  "Slaying Dragons",
  "Looking for lost d12",
  "Summoning the Dungeon Master",
  "Rolling initiative",
  "Calculating loot",
  "Drawing dungeon maps",
  "Choosing the perfect soundtrack",
  "Consulting the rulebook",
  "Painting miniatures",
  "Preparing epic monologues",
  "Creating plot twists",
  "Rolling for charisma",
  "Forging legendary weapons",
  "Conjuring mystical spells",
  "Gathering the party",
  "Setting up traps",
  "Deciphering ancient texts",
  "Bargaining with shopkeepers",
  "Finding a way out of the maze",
  "Escaping the dragon's lair",
  "Recruiting allies",
  "Negotiating with goblins",
  "Brewing healing potions",
  "Aligning the stars",
  "Calibrating mana levels",
  "Activating stealth mode",
  "Consulting the oracle",
  "Enchanting weapons",
  "Training pet dragons",
  "Sharpening swords",
  "Stitching wizard robes",
  "Taming wild unicorns",
  "Crafting magical amulets",
  "Reading ancient scrolls",
  "Studying spellbooks",
  "Polishing armor",
  "Scouting the enemy territory",
  "Preparing invisibility cloaks",
  "Fortifying defenses",
  "Casting protective wards",
  "Summoning spirit animals",
  "Chanting incantations",
  "Brewing storm clouds",
  "Decoding secret messages",
  "Rallying the troops",
  "Planning the banquet",
  "Cursing enemies",
  "Weaving dreams",
  "Consulting the stars",
  "Predicting the future",
  "Inventing new potions",
  "Exploring forgotten realms",
  "Mapping the astral plane",
  "Avoiding traps",
  "Setting up campfires",
  "Lighting torches",
  "Assembling the council",
  "Hiring mercenaries",
  "Negotiating treaties",
  "Writing heroic tales",
  "Crafting epic legends",
  "Planning surprise attacks",
  "Building secret passages",
  "Reading ancient runes",
  "Whispering to ancient trees",
  "Aligning magical ley lines",
  "Carving runes into stone",
  "Gathering moonlight in a jar",
  "Composing bardic ballads",
  "Refining elixirs of insight",
  "Forging alliances with fey",
  "Deciphering druidic symbols",
  "Orchestrating a thieves' guild meeting",
  "Plotting celestial alignments",
  "Unearthing lost artifacts",
  "Charting star maps",
  "Befriending wandering spirits",
  "Composing a hero's epic",
  "Designing secret lairs",
  "Inscribing mystical tomes",
  "Brewing ancient recipes",
  "Deciphering the language of dragons",
  "Setting up magical wards",
  "Collecting rare ingredients",
  "Assembling a circle of wizards",
  "Training with master swordsmen",
  "Solving riddles of the sphinx",
  "Tracking legendary beasts",
  "Preparing a royal coronation",
  "Staging a grand tournament",
  "Unraveling time paradoxes",
  "Hatching dragon eggs",
  "Casting divination rituals",
  "Hosting a war council",
  "Negotiating with elemental spirits",
  "Building enchanted constructs",
  "Exploring deep dungeons",
  "Conducting a knight's vigil",
  "Performing arcane experiments",
  "Planning a masquerade ball",
  "Uncovering hidden tombs",
  "Embarking on a quest for the holy grail",
  "Sailing uncharted waters",
  "Delving into forbidden knowledge",
  "Convening with celestial beings",
  "Navigating labyrinthine caves",
  "Reigniting ancient rivalries",
  "Restoring lost memories",
  "Summoning the winds of fate",
  "Traversing shadow realms",
  "Wielding elemental powers",
  "Disarming magical traps",
  "Binding ethereal entities",
  "Championing a lost cause",
  "Decoding the prophecy of ages",
  "Entwining fates and destinies",
  "Guarding sacred relics",
  "Journeying through dreamscapes",
  "Mastering the art of illusion",
  "Negotiating with the undead",
  "Unleashing arcane storms",
  "Venturing into the Feywild",
  "Waging wars of intrigue",
  "Channeling cosmic energies",
  "Dueling with rival mages",
  "Erecting mystical barriers",
  "Harvesting moonbeams",
  "Interpreting omens and signs",
  "Jousting in royal tournaments",
  "Kindling ancient fires",
  "Liberating enchanted prisons",
  "Mystifying audiences with magic shows",
  "Navigating the rivers of time",
  "Outsmarting trickster gods",
  "Pacifying raging elementals",
  "Questing for legendary swords",
  "Revealing hidden passageways",
  "Summoning ancestral spirits",
  "Trading with exotic merchants",
  "Unleashing genie from lamps",
  "Vanquishing shadowy fiends",
  "Warding off evil spirits",
  "X-raying mystical artifacts",
  "Yielding to ancient wisdom",
  "Zapping through arcane portals",
  "Arranging a truce with trolls",
  "Building bridges to other worlds",
  "Concocting invisibility brews",
  "Deducing the secrets of the universe",
  "Engraving runes of power",
  "Forming alliances with giants",
  "Grappling with moral dilemmas",
  "Heralding the arrival of heroes",
  "Illuminating the dark forests",
  "Joining forces with archangels",
  "Keening for fallen heroes",
  "Launching expeditions to the unknown",
  "Mending the fabric of reality",
  "Nurturing mythical creatures",
  "Observing celestial events",
  "Preserving ancient wisdom",
  "Questioning the morality of magic",
  "Restoring harmony to the land",
  "Shaping the course of destiny",
  "Teaching spells to apprentices",
  "Unveiling the secrets of alchemy",
  "Venerating ancient deities",
  "Waking sleeping dragons",
  "Xenoglossia with mystical beings",
  "Yoking the powers of nature",
  "Zealously guarding the sacred grove",
  "Aligning the Planar Gates",
  "Brewing Storms of Chaos",
  "Carving Runes of Destiny",
  "Dancing with Shadow Sprites",
  "Echoing Songs of the Ancients",
  "Fashioning Crowns of Stars",
  "Gilding Armour with Sunlight",
  "Hunting with Celestial Falcons",
  "Illuminating the Depths of the Underdark",
  "Juggling Fireballs of Fate",
  "Knitting the Webs of Time",
  "Lecturing at the Arcane University",
  "Mystifying Mazes with Magic",
  "Negotiating Peace with Dragons",
  "Orchestrating the Symphony of the Spheres",
  "Piloting Ships through Astral Seas",
  "Quelling Storms with a Whisper",
  "Revealing Secrets of the Eldritch Tome",
  "Studying Under Master Illusionists",
  "Translating the Songs of Merfolk",
  "Unveiling the Veil of the Void",
  "Voyaging to the Edge of Reality",
  "Whittling Wands from World Trees",
  "Exorcising Spirits from Haunted Keeps",
  "Yearning for Adventures Unknown",
  "Zoning Realms of Madness",
  "Animating Stone Golems",
  "Befriending Phoenixes in Ashes",
  "Conversing with Ancient Sphinxes",
  "Disarming Deadly Dungeon Devices",
  "Elevating Apprentices to Wizards",
  "Fostering Peace among Rival Kingdoms",
  "Guiding Lost Souls to Rest",
  "Harnessing the Power of Lightning",
  "Infiltrating Secret Societies",
  "Journeying to Lands Beyond Maps",
  "Kindling the Eternal Flame of Knowledge",
  "Learning the Lingo of Lycanthropes",
  "Manifesting Visions of the Future",
  "Negating Curses with a Smile",
  "Opening Portals to Forgotten Lands",
  "Pondering Philosophies of the Multiverse",
  "Quarreling with Quirky Quasits",
  "Resurrecting Ancient Heroes",
  "Shielding Villages from Vengeful Spirits",
  "Taming the Tides with Triton's Conch",
  "Unraveling the Mysteries of the Monolith",
  "Venturing Beyond the Veil of Death",
  "Wielding the Whispering Blade",
  "Xenobotany in the Enchanted Forest",
  "Yodeling in Dwarvish Caverns",
  "Zeroing In on Zodiac Secrets",
  "Ambushing Bandits in Moonlit Woods",
  "Bargaining with the Lords of the Abyss",
  "Crafting Keys to Hidden Kingdoms",
  "Delving into the Heart of the Mountain",
  "Embarking on Epic Odysseys",
  "Fencing with Fabled Swordmasters",
  "Galloping with Ghostly Steeds",
  "Heralding the Dawn of New Ages",
  "Imbuing Stones with Starlight",
  "Jesting with Jovial Giants",
  "Keeping Vigil over Ancient Ruins",
  "Luring Leviathans from the Deep",
  "Mastering the Melodies of Magic",
  "Navigating the Nexus of Nightmares",
  "Outwitting Ogres in Chess",
  "Pacifying the Spirits of the Wild",
  "Questing for the Quartz of Clarity",
  "Restoring the Rings of Harmony",
  "Summoning the Seraphs of Sunrise",
  "Twisting the Threads of Fate",
  "Uncovering the Underworld's Secrets",
  "Vibrating with the Voice of the Void",
  "Wrestling with the Winds of Winter",
  "Exalting the Exarchs of Excellence",
  "Yielding to the Yarns of Yesteryears"
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
    document.getElementById('campaignMessage').text = message;
    document.getElementById('loadingModal').style.display = 'flex';
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
            //update currently processing campaign
            updateCurrentlyProcessing();
        }
async function updateCurrentlyProcessing() {
     let listElement = document.getElementById('currentlyProcessing');
     listElement.innerHTML = '';
    try {
        let response = await fetch('/queue/requests/current');
        let data = await response.json();
       
        

        if (data) {
            let listItem = document.createElement('li');
            listItem.textContent = data.title; // Assuming the response data is the text to be displayed
            listElement.appendChild(listItem);
        } else {
            listElement.innerHTML = 'Nothing';
        }
    } catch (error) {
        let listItem = document.createElement('li');
            listItem.textContent = 'Nothing'; // Assuming the response data is the text to be displayed
        console.error('Error fetching current request:', error);
    }
}

async function fetchStory() {
    try {
        const response = await fetch('/prompt/story');
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        const text = await response.text();
        return text;
    } catch (error) {
        console.error('Error fetching story:', error);
        return '';
    }
}