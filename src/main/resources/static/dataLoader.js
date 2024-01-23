
// JavaScript for loading data into dropdowns from JSON files in the 'data' folder and for adding dynamic sections

function loadDataIntoDropdown(dropdownId, dataUrl) {
    if(dataUrl != 'unknown')
    {
    fetch(dataUrl)
        .then(response => response.json())
        .then(data => {
            const dropdown = document.getElementById(dropdownId);
            let option = document.createElement('option');
                option.value = 'Random';
                option.text ='Random';
                dropdown.appendChild(option);

            data.forEach(item => {
                let option = document.createElement('option');
                option.value = item;
                option.text = item;
                dropdown.appendChild(option);
            });
        })
        .catch(error => console.error('Error loading data:' + dataUrl, error));
    }
}
function getDataTypeForDropdown(dropdownId)
{
    var ddBaseId = dropdownId;
    if(ddBaseId.includes("_")) ddBaseId = dropdownId.split('_')[0];
    switch (ddBaseId) {
        case 'preferredSetting': return 'data/Settings.json'; break;
        case 'campaignTone': return 'data/CampaignTones.json';break;
        case 'areaType': return 'data/areaType.json';break;
        case 'npcType': return 'data/NPCType.json';break;
        case 'npcRole': return 'data/NPCRole.json';break;
        case 'npcWeapon': return 'data/Weapons.json';break;
        case 'npcArmor': return 'data/ArmourType.json';break;
        case 'characterSpecialty': return 'data/CharacterSpecialty.json';break;
        case 'npcRace': 
        case 'characterRace': 
            return 'data/Races.json';break;
        case 'npcClass':
        case 'characterClass':
            return 'data/Classes.json';break;
        default:
            return 'unknown';

        
      }
}

function loadDataForDropDown(dropdownId)
{
    loadDataIntoDropdown(dropdownId,getDataTypeForDropdown(dropdownId));
}



// Load data into dropdowns for Basic Campaign Details
loadDataForDropDown('preferredSetting');
loadDataForDropDown('campaignTone')
loadDataForDropDown('areaType');

// Load data into dropdowns for NPC Character
loadDataForDropDown('npcType');
loadDataForDropDown('npcRace');
loadDataForDropDown('npcClass');
loadDataForDropDown('npcRole');
loadDataForDropDown('npcWeapon');
loadDataForDropDown('npcArmor');

// Load data into dropdowns for Party Characters
loadDataForDropDown('characterClass');
loadDataForDropDown('characterRace');
loadDataForDropDown('characterSpecialty');

// Load data into dropdown for Campaign Type
loadDataForDropDown('campaignType');
