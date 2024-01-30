# DNDGenerate


<img src="https://github.com/dhorions/backend/blob/main/src/main/resources/static/logo_trans.png?raw=true" width="200"/>

## Scroll of Introduction
DnDGenerate is an AI tool to convert you idea in a DnD Scenario.

## Enchanted Features
- **Campaign Creation:** Provide your idea, as simple or elaborate as you want, a campaign will be generated, with setting, goals, locations, challenges, NPC description and stats will be generated.
- **NPC Images:** For each of the NPC's in your campaign, an image will be generated.
- **PDF download:** The campaign will be downloadable as a pdf.

## Alchemist's Setup
### Run with Docker 
```bash
# Instructions for concocting the perfect setup
docker run --name DnDGenerate quodlibetbe/dndgenerate -e OPENAI_API_KEY=[YOUR_OPENAI_API_KEY] -p 8080:8080
```
### Build with Docker
If you build the java project locally and make changes, use this command to create a docker image.

```bash
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
If you want to contribute to this project, creating a pull request is the way to go.

## Scroll of License
[MPL-2.0](https://github.com/dhorions/DnDGenerate/blob/main/LICENSE)

## Hall of Acknowledgments
- dhorions
- The Enigmatic ChatGPT: An arcane entity from the digital realm, tirelessly weaving the fabric of code with the finesse of a master spellcaster. Rumor has it that most of the mystical script was conjured by its ethereal keystrokes.
