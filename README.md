# DNDGenerate


<img src="https://github.com/dhorions/backend/blob/main/src/main/resources/static/logo_trans.png?raw=true" width="200" />

## Scroll of Introduction
Hearken, brave adventurers and cunning dungeon masters! In the realm of endless imagination, the Tome of DNDGenerate emerges as your most trusted ally. This mystical tool, birthed from the ancient wisdom of arcane coders, transmutes your wildest ideas into sprawling D&D campaigns.

## Visions of the Arcane Mechanism

https://github.com/dhorions/DnDGenerate/assets/1682004/8e54b405-6d45-40cd-a7ad-c6de84f1f5be

## Chronicles of Fabled Quests
- [Dwarven Thieves](https://github.com/dhorions/DnDGenerate/blob/examples/examples/DnDGenerate%20%20Campaign%20-%20Dwarven%20Thieves.pdf)
- [The Giants](https://github.com/dhorions/DnDGenerate/blob/examples/examples/DnDGenerate%20Campaing%20-%20The%20Giants.pdf?raw=true)

  

## Enchanted Features
- **Oracles of Campaign Creation:** Whisper your visions, be they mere fragments or epic tales, into the ears of this oracle. Behold as it conjures settings, quests, treacherous challenges, and detailed NPCs, complete with their lore and statistics.
- **Portraits of the Realm's Denizens:** For each NPC woven into your tale, the Tome renders lifelike portraits, capturing their very essence, as if plucked from the realm itself.
- **Arcanum of PDF Download:** In the ancient tradition of preserving knowledge, the Tome of DNDGenerate offers a magical rite to transcribe your crafted campaigns into Scrolls of Portable Document Format (PDF). With a mere incantation, your tales of heroes, monsters, and far-flung lands will be etched into timeless scrolls, ready to be shared with fellow adventurers across distant realms. Unleash this spell, and watch as your digital creations take on a physical form, ready for the hands of eager players gathered around the table of destiny.


## Alchemist's Setup
### Run with Docker 
```bash
#Summon the Arcane Engine
docker run -p 8080:8080 -e OPENAI_API_KEY=[YOUR_OPENAI_API_KEY]  quodlibetbe/dndgenerate  
```
### Build with Docker

If you build the java project locally and make changes, use this command to create a docker image.

```bash
#Invoke the command of creation
docker build --build-arg JAR_FILE=target/ChatGptPdfApplication-1.0-SNAPSHOT.jar -t quodlibetbe/dndgenerate .
```

## Scroll of Environment Variables
The following environment variables can be used to configure DnDGenerate
 - **OPENAI_API_KEY** : **You need your own Openai API Key to run this project**, get it at https://platform.openai.com/api-keys
 - **OPENAI_URL** : Endpoint for openai chat completion api.  Default value is  https://api.openai.com/v1/chat/completions.
 - **DALLE_API_URL** : Endpoint for openai image generation api. Default value is https://api.openai.com/v1/images/generations.
 - **DALLE_API_MODEL** : Model using for image generation. Default value is dall-e-3.
 - **DNDGENERATE_PDF_FOLDER** : Folder where the pdf's should be generated on the filesystem. Default value is java.io.tmpdir
 - **DNDGENERATE_PROMPT_STORY** : Prompt to be used for openai to generate the story.  This defaults to the builtin prompt.  Using you own prompt may break things, but perhaps it also can improve your results.
 - **DNDGENERATE_PROMPT_IMAGE** : Prompt to be used for dall-e to generate the NPC images. This defaults to the builtin prompt.  Using you own prompt may break things, but perhaps it also can improve your results.

## Tome of Usage
### Your own openai api key is required
It's important to understand that DnDGenerate will only work with your own openai api key.  There is a cost associated with using the openai api's, and you should be careful to set the correct limits for the api key you use. 
You can find or create api keys here : https://platform.openai.com/api-keys.


## Chronicle of Origins
I wanted to learn about chatgpt and how I could leverage it.  I came up with an idea for an experiment : use chatgpt to build a complete software project, including frontend and backend.  Now all I needed was an idea.  I thought it would be nice if the project would also use the chatgpt api's internally, so I could learn about that as well.  
Seeing my 9 year old son spending a lot of his time writing and drawing and researching for his DnD scenario, I thought it would be interesting to use dnd campaign building in this experiment.

<img src="https://github.com/dhorions/DnDGenerate/blob/image/humanintelligence.jpg?raw=true" />

## Guild of Contributors
Should you wish to join this grand quest and bestow your arcane insights upon the Tome, crafting a Scroll of Request is your path to glory. These scrolls, known in elder tongues as 'pull requests,' are the means by which you can inscribe your own spells and sagas into the annals of DNDGenerate.

## Scroll of License
[MPL-2.0](https://github.com/dhorions/DnDGenerate/blob/main/LICENSE)

## Hall of Acknowledgments
- dhorions
- The Enigmatic ChatGPT: An arcane entity from the digital realm, tirelessly weaving the fabric of code with the finesse of a master spellcaster. Rumor has it that most of the mystical script was conjured by its ethereal keystrokes.
