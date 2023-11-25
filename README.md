---
layout: editorial
---

# Specification

This specification defines a format outlining a set of rules for custom sky rendering. These rules can be organized into 11 groups. Please refer to the side panel for the table of contents.

To start writing your configuration for your skybox, create a text file, and save it as a `.json`. The format starts with a `{`  and ends with a `}`. In between you can start adding your parameters from the 11 groups, called `objects`. Each object is separated by a `,` and can be be arranged in any order.

Throughout the document, you will find examples. Additionally, at the bottom of the page, you'll find comprehensive examples of various skybox types, demonstrating the structure of a complete file. Template files are also provided to assist you in starting your own pack quickly.

## 1. Schema version

The current version is 2, but this may change in the future. The mod isn't yet stable since we are in the `0.x` phase. This object is required.

```json
"schemaVersion": 2
```

## 2. Type

There are three main types of skyboxes: monocolor skyboxes, textured skyboxes, and vanilla skyboxes. One of the 3 types is required.

### 2.1 Monocolor skyboxes

<table><thead><tr><th width="240" align="center">Name</th><th align="center">Description</th></tr></thead><tbody><tr><td align="center"><code>monocolor</code></td><td align="center">Shows a single plain color for the full skybox.</td></tr></tbody></table>

```json
"type": "monocolor"
```

### 2.2 Textured skyboxes

<table><thead><tr><th width="374" align="center">Name</th><th align="center">Description</th></tr></thead><tbody><tr><td align="center"><code>square-textured</code></td><td align="center">uses 6 separate 1:1 aspect ratio texture files for the 6 sides of the skybox</td></tr><tr><td align="center"><code>animated-square-textured</code></td><td align="center">uses multiple sets of 6 separate 1:1 aspect ratio texture files for an animated skybox</td></tr><tr><td align="center"><code>single-sprite-square-textured</code></td><td align="center">uses a single 3:2 aspect ratio texture file for the skybox</td></tr><tr><td align="center"><code>single-sprite-animated-square-textured</code></td><td align="center">uses multiple 3:2 aspect ratio texture files for an animated skybox</td></tr><tr><td align="center"><code>multi-texture</code></td><td align="center">uses a grid of textures- arranged in a single texture atlas- for an animation</td></tr></tbody></table>

```json
"type": "single-sprite-square-textured"
```

Example for `(animated-)square-textured` skybox

<figure><img src=".gitbook/assets/separate-sprites.png" alt=""><figcaption></figcaption></figure>

Example for `single-sprite-(animated-)square-textured` skybox

<figure><img src=".gitbook/assets/single-sprite.png" alt=""><figcaption></figcaption></figure>

Here's an example of an animation texture used for the `multi-texture` skybox type. Frames should be ordered from top to bottom, then left to right as seen below. Each frame can have any size and aspect ratio. However, the combined size of the texture should not exceed 8192x8192 pixels. Only very recent GPUs can handle textures up to 16384x16384 pixels.

<figure><img src=".gitbook/assets/multi-texture.png" alt=""><figcaption></figcaption></figure>

### 2.3 Vanilla skyboxes

<table><thead><tr><th width="211" align="center">Name</th><th align="center">Description</th></tr></thead><tbody><tr><td align="center"><code>overworld</code></td><td align="center">This will show the overworld's skybox.</td></tr><tr><td align="center"><code>end</code></td><td align="center">This will show the end's skybox.</td></tr></tbody></table>

```json
"type": "overworld"
```

## 3. Color

This is exclusive to and is a requirement when using the `monocolor` type of skybox.

<table><thead><tr><th width="126" align="center">Name</th><th align="center">Description</th><th width="108" align="center">Required</th><th width="100" align="center">Default</th></tr></thead><tbody><tr><td align="center"><code>red</code></td><td align="center">Specifies the amount of red color to be used. Must be a value between 0 and 1.</td><td align="center">✅</td><td align="center">-</td></tr><tr><td align="center"><code>green</code></td><td align="center">Specifies the amount of green color to be used. Must be a value between 0 and 1.</td><td align="center">✅</td><td align="center">-</td></tr><tr><td align="center"><code>blue</code></td><td align="center">Specifies the amount of blue color to be used. Must be a value between 0 and 1.</td><td align="center">✅</td><td align="center">-</td></tr><tr><td align="center"><code>alpha</code></td><td align="center">Specifies the amount of alpha transparency to be used. Must be a value between 0 and 1.</td><td align="center">❌</td><td align="center"><code>1.0</code></td></tr></tbody></table>

```json
"color": {"red": 0.84, "green": 0.91, "blue": 0.72, "alpha": 1.0}
```

## 4. Texture

This is exclusive to and is a requirement for the two **non**-animated skybox types.

<table><thead><tr><th width="139" align="center">Name</th><th align="center">Description</th></tr></thead><tbody><tr><td align="center"><code>texture</code></td><td align="center">Used for the <code>single-sprite-square-textured</code> skybox type. Specifies the directory for the texture file.</td></tr><tr><td align="center"><code>textures</code></td><td align="center">Used for the <code>square-textured</code> skybox type. Specifies the directory for the texture files to be used for the <code>top</code>, <code>bottom</code>, <code>east</code>, <code>west</code>, <code>north</code> and <code>south</code> sides of the skybox.</td></tr></tbody></table>

```json
"texture": "fabricskyboxes:sky/skybox.png"
```

OR

```json
"textures": {
  "top": "fabricskyboxes:sky/skybox_top.png",
  "bottom": "fabricskyboxes:sky/skybox_bottom.png",
  "east": "fabricskyboxes:sky/skybox_east.png",
  "west": "fabricskyboxes:sky/skybox_west.png",
  "north": "fabricskyboxes:sky/skybox_north.png",
  "south": "fabricskyboxes:sky/skybox_south.png"
}
```

## 5. Animations

This is exclusive to and is a requirement for the `multi-texture` skybox type. You can incorporate as many animations as needed.&#x20;

The animation texture will cycle through within the designated animation area, determined by the UV ranges. The number of steps in the cycle is defined by the number of rows and columns on the animation texture.

<table><thead><tr><th align="center">Name</th><th width="373" align="center">Description</th><th align="center">Required</th></tr></thead><tbody><tr><td align="center"><code>texture</code></td><td align="center">Specifies the location of the texture to be used when rendering the animation</td><td align="center">✔️</td></tr><tr><td align="center"><code>uvRanges</code></td><td align="center">Specifies the location in UV ranges to render the animation</td><td align="center">✔️</td></tr><tr><td align="center"><code>gridColumns</code></td><td align="center">Specifies the amount of columns the animation texture has</td><td align="center">✔️</td></tr><tr><td align="center"><code>gridRows</code></td><td align="center">Specifies the amount of rows the animation texture has</td><td align="center">✔️</td></tr><tr><td align="center"><code>duration</code></td><td align="center">Specifies the default duration of each animation frame in milliseconds</td><td align="center">✔️</td></tr><tr><td align="center"><code>frameDuration</code></td><td align="center">Specifies the specific duration per animation frame, which overrides <code>duration</code></td><td align="center">❌</td></tr></tbody></table>

Example for a skybox with 2 animations

```json
"animations": [
  {
    "texture": "fabricskyboxes:/sky/anim_texture_1.png",
    "uvRanges": {
      "minU": 0.25,
      "minV": 0.25,
      "maxU": 0.5,
      "maxV": 0.5
    },
    "gridColumns": 2,
    "gridRows": 4,
    "duration": 50,
    "frameDuration": {"1": 20, "5": 10}
  },
  {
    "texture": "fabricskyboxes:/sky/anim_texture_2.png",
    "uvRanges": {
      "minU": 0.3333,
      "minV": 0.6666,
      "maxU": 0.2,
      "maxV": 0.4
    },
    "gridColumns": 3,
    "gridRows": 5,
    "duration": 10,
    "frameDuration": {"1": 30, "5": 40}
  }
]
```

### 5.1 UV ranges

Specifies a UV range object for defining texture coordinates. All fields are required.

<table><thead><tr><th width="257" align="center">Name</th><th align="center">Description</th></tr></thead><tbody><tr><td align="center"><code>minU</code></td><td align="center">Specifies the minimum U coordinate</td></tr><tr><td align="center"><code>minV</code></td><td align="center">Specifies the minimum V coordinate</td></tr><tr><td align="center"><code>maxU</code></td><td align="center">Specifies the maximum U coordinate</td></tr><tr><td align="center"><code>maxV</code></td><td align="center">Specifies the maximum V coordinate</td></tr></tbody></table>

Presently, only the 3:2 aspect ratio skybox template is supported, as shown in the example below. Importantly, the aspect ratio of your animated area does not necessarily need to match the aspect ratio of the frames in your animation texture. Consequently, your frames will be stretched to fit the animation area, even if the aspect ratios do not align.

<figure><img src=".gitbook/assets/UV example.png" alt=""><figcaption></figcaption></figure>

### 5.2 Frame duration

Overrides the default duration of each frame that is set by `duration`, allowing unique duration for each frame in the animation. All fields are optional. The total number of frames specified should not exceed the number of frames included on the animation texture.

| Name |                            Description                            |
| :--: | :---------------------------------------------------------------: |
|  `1` | Specifies the duration of the 1st animation frame in milliseconds |
|  `2` | Specifies the duration of the 2nd animation frame in milliseconds |
|  `3` | Specifies the duration of the 3rd animation frame in milliseconds |
|  `4` | Specifies the duration of the 4th animation frame in milliseconds |

## 6. Animation textures

This is exclusive to and a requirement for the two animated skybox types. Depending on the chosen skybox type, you will need to specify the textures differently.&#x20;

Below are the steps for `animated-square-textured` skyboxes, using a 3-frame animation as an example.

```json
"animationTextures": [
  {
    "top": "fabricskyboxes:sky/skybox_frame1_top.png",
    "bottom": "fabricskyboxes:sky/skybox_frame1_bottom.png",
    "east": "fabricskyboxes:sky/skybox_frame1_east.png",
    "west": "fabricskyboxes:sky/skybox_frame1_west.png",
    "north": "fabricskyboxes:sky/skybox_frame1_north.png",
    "south": "fabricskyboxes:sky/skybox_frame1_south.png"
  },
  {
    "top": "fabricskyboxes:sky/skybox_frame2_top.png",
    "bottom": "fabricskyboxes:sky/skybox_frame2_bottom.png",
    "east": "fabricskyboxes:sky/skybox_frame2_east.png",
    "west": "fabricskyboxes:sky/skybox_frame2_west.png",
    "north": "fabricskyboxes:sky/skybox_frame2_north.png",
    "south": "fabricskyboxes:sky/skybox_frame2_south.png"
  },
  {
    "top": "fabricskyboxes:sky/skybox_frame3_top.png",
    "bottom": "fabricskyboxes:sky/skybox_frame3_bottom.png",
    "east": "fabricskyboxes:sky/skybox_frame3_east.png",
    "west": "fabricskyboxes:sky/skybox_frame3_west.png",
    "north": "fabricskyboxes:sky/skybox_frame3_north.png",
    "south": "fabricskyboxes:sky/skybox_frame3_south.png"
  }
]
```

And for the `single-sprite-animated-square-textured` skybox type.

```json
"animationTextures": [
  "fabricskyboxes:sky/skybox_frame1.png",
  "fabricskyboxes:sky/skybox_frame2.png",
  "fabricskyboxes:sky/skybox_frame3.png"
]
```

## 7. FPS

This is exclusive to and a requirement for the two animated skybox types.

<table><thead><tr><th width="220" align="center">Name</th><th align="center">Description</th></tr></thead><tbody><tr><td align="center"><code>fps</code></td><td align="center">Specifies the number of frames to be rendered per second.</td></tr></tbody></table>

```json
"fps": 1
```

## 8. Blend

Specifies how the skybox should blend with the previously rendered sky layer, with the vanilla skybox being the first layer. All fields in this specification are optional.

In FabricSkyboxes, the blending of textured skyboxes is achieved using `glBlendFunc`. The provided blending types serve as presets, utilizing the same equations outlined below for the custom type. For `decorations`, the blending used aligns with the vanilla game's approach, particularly for the sun and moon blending.

<table><thead><tr><th width="133" align="center">Name</th><th width="476" align="center">Description</th><th align="center">Default</th></tr></thead><tbody><tr><td align="center"><code>type</code></td><td align="center">Specifies the type of the blend for the skybox only. Valid types are: <code>add</code>, <code>subtract</code>, <code>multiply</code>, <code>screen</code>, <code>replace</code>, <code>alpha</code>, <code>dodge</code>, <code>burn</code>, <code>darken</code>, <code>lighten</code>, <code>decorations</code>, <code>disable</code> and <code>custom</code>.</td><td align="center"><code>add</code></td></tr><tr><td align="center"><code>blender</code></td><td align="center">only use this when using the <code>custom</code> blend type</td><td align="center"></td></tr></tbody></table>

```json
"blend": {
  "type" : "alpha"
}
```

OR

```json
"blend": {
  "type" : "custom",
  "blender": {
    "sFactor": 0,
    "dFactor": 769,
    "equation": 32774,
    "redAlphaEnabled": true,
    "greenAlphaEnabled": true,
    "blueAlphaEnabled": true,
    "alphaEnabled": false
  }
}
```

<table><thead><tr><th width="222" align="center">Name</th><th align="center">Description</th><th width="100" align="center">Default</th></tr></thead><tbody><tr><td align="center"><code>sFactor</code></td><td align="center">Specifies the OpenGL source factor to use.</td><td align="center"></td></tr><tr><td align="center"><code>dFactor</code></td><td align="center">Specifies the OpenGL destination factor to use.</td><td align="center"></td></tr><tr><td align="center"><code>equation</code></td><td align="center">Specifies the OpenGL blend equation to use.</td><td align="center"></td></tr><tr><td align="center"><code>redAlphaEnabled</code></td><td align="center">Specifies whether alpha state will be used in red shader color or predetermined value of 1.0.</td><td align="center"><code>false</code></td></tr><tr><td align="center"><code>greenAlphaEnabled</code></td><td align="center">Specifies whether alpha state will be used in green shader color or predetermined value of 1.0.</td><td align="center"><code>false</code></td></tr><tr><td align="center"><code>blueAlphaEnabled</code></td><td align="center">Specifies whether alpha state will be used in blue shader color or predetermined value of 1.0.</td><td align="center"><code>false</code></td></tr><tr><td align="center"><code>alphaEnabled</code></td><td align="center">Specifies whether alpha state will be used in shader color or predetermined value of 1.0.</td><td align="center"><code>false</code></td></tr></tbody></table>

More information on custom blend can be found in the [blend documentation](https://github.com/AMereBagatelle/fabricskyboxes/blob/1.19.x-dev/docs/blend.md).

## 9. Properties

Specifies common properties used by all types of skyboxes.

<table><thead><tr><th width="260" align="center">Name</th><th align="center">Description</th><th width="109" align="center">Required</th><th align="center">Default</th></tr></thead><tbody><tr><td align="center"><code>priority</code></td><td align="center">Specifies the order which skybox will be rendered. If there are multiple skyboxes with identical priority, those skyboxes are not re-ordered therefore being dependant of Vanilla's alphabetical namespaced identifier's loading.</td><td align="center">❌</td><td align="center"><code>0</code></td></tr><tr><td align="center"><code>fade</code></td><td align="center">Specifies the time of day in ticks that the skybox should start and end fading in and out.</td><td align="center">✔️</td><td align="center">-</td></tr><tr><td align="center"><code>rotation</code></td><td align="center">Specifies the rotation speed and angles of the skybox.</td><td align="center">❌</td><td align="center"><code>[0,0,0]</code> for <code>static</code>/<code>axis</code>, <code>0</code> for <code>rotationSpeedX/Y/Z</code>, <code>true</code> for <code>skyboxRotation</code></td></tr><tr><td align="center"><code>transitionInDuration</code></td><td align="center">Specifies the duration in ticks that skybox will fade in when valid conditions are changed. The value must be within 1 and 8760000 (365 days * 24000 ticks).</td><td align="center">❌</td><td align="center"><code>20</code></td></tr><tr><td align="center"><code>transitionOutDuration</code></td><td align="center">Specifies the duration in ticks that skybox will fade out when valid conditions are changed. The value must be within 1 and 8760000 (365 days * 24000 ticks).</td><td align="center">❌</td><td align="center"><code>20</code></td></tr><tr><td align="center"><code>changeFog</code></td><td align="center">Specifies whether the skybox should change the fog color.</td><td align="center">❌</td><td align="center"><code>false</code></td></tr><tr><td align="center"><code>fogColors</code></td><td align="center">Specifies the colors to be used for rendering fog. Only has an effect if changeFog is true.</td><td align="center">❌</td><td align="center"><code>0</code> for each value</td></tr><tr><td align="center"><code>sunSkyTint</code></td><td align="center">Specifies whether the skybox should disable sunrise/set sky color tinting.</td><td align="center">❌</td><td align="center"><code>true</code></td></tr><tr><td align="center"><code>inThickFog</code></td><td align="center">Specifies whether the skybox should be rendered in thick fog.</td><td align="center">❌</td><td align="center"><code>true</code></td></tr><tr><td align="center"><code>minAlpha</code></td><td align="center">Specifies the minimum value that the alpha can be. The value must be within 0 and 1.</td><td align="center">❌</td><td align="center"><code>0.0</code></td></tr><tr><td align="center"><code>maxAlpha</code></td><td align="center">Specifies the maximum value that the alpha can be. The value must be within 0 and 1.</td><td align="center">❌</td><td align="center"><code>1.0</code></td></tr></tbody></table>

```json
"properties": {
  "priority": 1,
  "fade": {
    "startFadeIn": 1000,
    "endFadeIn": 2000,
    "startFadeOut": 3000,
    "endFadeOut": 4000,
    "alwaysOn": false
  },
  "rotation": {
    "rotationSpeedY": 0.866,
    "static": [0.0, 0.0, 0.0],
    "axis": [0.0, -180.0, 0.0],
    "timeShift": [0, 0, 0],
    "skyboxRotation": "true"
  },
  "transitionInDuration": 200,
  "transitionOutDuration": 300,
  "changeFog": true,
  "fogColors": {"red": 0.84, "green": 0.91, "blue": 0.97, "alpha": 1.0},
  "sunSkyTint": true,
  "inThickFog": true,
  "minAlpha": 0.1,
  "maxAlpha": 0.9
}
```

As you can see, `fade`, `rotation` (and its `static` and `axis` components) have multiple object within, so let's take a look at those in more detail. For `fogColors`, refer to [Color](./#3.-color) for the specification.

### 9.1 Fade object

Stores a list of four integers which specify the time in ticks to start and end fading the skybox in and out.

<table><thead><tr><th align="center">Name</th><th align="center">Description</th><th width="108" align="center">Required</th><th width="100" align="center">Default</th></tr></thead><tbody><tr><td align="center"><code>startFadeIn</code></td><td align="center">The times in ticks when a skybox will start to fade in.</td><td align="center">✔️ | ❌<br></td><td align="center">-</td></tr><tr><td align="center"><code>endFadeIn</code></td><td align="center">The times in ticks when a skybox will end fading in.</td><td align="center">✔️ | ❌</td><td align="center">-</td></tr><tr><td align="center"><code>startFadeOut</code></td><td align="center">The times in ticks when a skybox will start to fade out.</td><td align="center">✔️ | ❌</td><td align="center">-</td></tr><tr><td align="center"><code>endFadeOut</code></td><td align="center">The times in ticks when a skybox will end fading out.</td><td align="center">✔️ | ❌</td><td align="center">-</td></tr><tr><td align="center"><code>alwaysOn</code></td><td align="center">Whether the skybox should always be at full visibility.</td><td align="center">❌ | ✔️</td><td align="center"><code>false</code></td></tr></tbody></table>

`Fade` and `alwaysOn` can be use exclusively.

Conversion table

<table><thead><tr><th width="154" align="center">Time in Ticks</th><th width="143" align="center">Clock Time</th></tr></thead><tbody><tr><td align="center">0</td><td align="center">6 AM</td></tr><tr><td align="center">6000</td><td align="center">12 AM</td></tr><tr><td align="center">12000</td><td align="center">6 PM</td></tr><tr><td align="center">24000</td><td align="center">12 PM</td></tr></tbody></table>

### 9.2 Rotation Object

Specifies the speed, static- and axis of rotation for a skybox. This object is used by both [Properties](./#8.-properties) and [Decorations](./#10.-decorations). Properties only affects the skybox rotation, and Decorations only affect the sun, moon and stars rotation. All fields are optional.

<table><thead><tr><th width="230" align="center">Name</th><th width="336" align="center">Description</th><th align="center">Default</th></tr></thead><tbody><tr><td align="center"><code>static</code></td><td align="center">Specifies the static rotation in degrees</td><td align="center"><code>[0,0,0]</code></td></tr><tr><td align="center"><code>axis</code></td><td align="center">Specifies the axis rotation in degrees</td><td align="center"><code>[0,0,0]</code></td></tr><tr><td align="center"><code>timeShift</code></td><td align="center">Specifies the time shifted for rotation</td><td align="center"><p><code>[0,0,0]</code></p><p>(integers only)</p></td></tr><tr><td align="center"><code>rotationSpeedX/Y/Z</code></td><td align="center">Specifies the speed of the skybox rotation around the X, Y or Z axis, in rotations per 24000 ticks</td><td align="center"><code>0</code></td></tr><tr><td align="center"><code>skyboxRotation</code></td><td align="center">During sunset/rise, the rotation speed for the sun/moon will slow down/speed up. This object can toggle this behavior.</td><td align="center"><code>true</code> for <code>properties</code>, <code>false</code> for <code>decorations</code></td></tr></tbody></table>

Static rotation should be envisioned as the initial or 'default' rotation of the skybox, before any active rotation is applied. The axis refers to the actual axis around which the skybox will visibly revolve. The speed parameter determines how many full rotations the sky makes in a single in-game day.

`timeShift` facilitates synchronization between the skyboxes utilized by both FabricSkyBoxes and OptiFine. By default, FabricSkyBoxes initiates rotation at 0 tick time, whereas OptiFine begins at 18000 tick time.

`skyboxRotation` becomes noticeable when the time is set to 12000 or 0. During sunrise and sunset, the sun/moon will be slightly above the horizon. This represents the default behavior for decorations when skyboxRotation is set to false. If set to true, the sun/moon will align exactly with the horizon line at times 12000 and 0. This adjustment ensures that the rotation parameters function similarly for both the skyboxes and the sun/moon, enhancing the reliability of rotation configuration.

### 9.3 Float vector

Specifies a list of three floating-point literals to represent degrees of rotation.

To get a better understanding on how to define these degrees for static and axis, take a look at the image below.

<figure><img src=".gitbook/assets/axis.png" alt=""><figcaption></figcaption></figure>

Additionally, a Blender tool has been developed courtesy of @UsernameGeri, allowing real-time visualization and adjustment of both static rotation and axis, providing insights into their impact on the skybox's rotation. This tool is compatible with both FabricSkyBoxes and OptiFine.

Comprehensive instructions on utilizing the tool are documented within the blend file. Basic proficiency in Blender, encompassing simple actions like camera movement and navigation within the interface, is all that's required to effectively utilize this tool. Mastery of these fundamental skills can be acquired through brief tutorials available on platforms like YouTube, typically taking less than 10 minutes to grasp.

{% file src=".gitbook/assets/skybox rotation.blend" %}

Blender is a free, open source software. Download at [https://www.blender.org/](https://www.blender.org/)

## 10. Conditions

Specifies when and where a skybox should render. All fields are optional.

<table><thead><tr><th width="154" align="center">Name</th><th align="center">Description</th></tr></thead><tbody><tr><td align="center"><code>worlds</code></td><td align="center">Specifies a list of worlds sky effects that the skybox should be rendered in.</td></tr><tr><td align="center"><code>dimensions</code></td><td align="center">Specifies a list of dimension that the skybox should be rendered in.</td></tr><tr><td align="center"><code>weather</code></td><td align="center">Specifies a list of weather conditions that the skybox should be rendered in. Valid entries are <code>clear</code>, <code>rain</code>, <code>biome_rain</code>, <code>thunder</code> and <code>snow</code>.</td></tr><tr><td align="center"><code>biomes</code></td><td align="center">Specifies a list of biomes that the skybox should be rendered in.</td></tr><tr><td align="center"><code>xRanges</code></td><td align="center">Specifies a list of coordinates that the skybox should be rendered between.</td></tr><tr><td align="center"><code>yRanges</code></td><td align="center">Specifies a list of coordinates that the skybox should be rendered between.</td></tr><tr><td align="center"><code>zRanges</code></td><td align="center">Specifies a list of coordinates that the skybox should be rendered between.</td></tr><tr><td align="center"><code>loop</code></td><td align="center">Specifies the loop object that the skybox should be rendered between.</td></tr><tr><td align="center"><code>effects</code></td><td align="center">Specifies a list of player status effects during which the skybox should be rendered.</td></tr></tbody></table>

```json
"conditions": {
  "worlds": ["minecraft:overworld"],
  "dimensions": ["my_datapack:custom_world"],
  "weather": ["rain", "thunder"],
  "biomes": ["plains", "forest", "river"],
  "xRanges": [{"min": -100.0, "max": 100.0}],
  "yRanges": [{"min": -128.0, "max": 150.0}, {"min": 200.0, "max": 320.0}],
  "zRanges": [{"min": -150.0, "max": 150.0}],
  "loop": {"days": 8, "ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]},
  "effects": ["minecraft:jump_boost", "minecraft:speed", "minecraft:slowness"]
}
```

Similarly to Properties, some conditions have multiple objects within. Let's take a look at them.

### 10.1 MinMax Entry Object

These objects are used by the `x-y-z range` objects, and the `loop` object. Multiple `MinMax` entries can be specified within one range.

<table><thead><tr><th width="183" align="center">Name</th><th align="center">Description</th></tr></thead><tbody><tr><td align="center"><code>min</code></td><td align="center">Specifies the minimum value, inclusive</td></tr><tr><td align="center"><code>max</code></td><td align="center">Specifies the maximum value, exclusive</td></tr></tbody></table>

These `MinMax` ranges should be thought of as sections on a number line, rather than incremental steps, like blocks in Minecraft. This means, that you can specify them to a decimal point precision.

To show a concrete example, let's take a look at the day ranges of a loop object.

```json
"loop": {
  "days": 8,
  "ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
}
```

<figure><img src=".gitbook/assets/ranges.png" alt=""><figcaption></figcaption></figure>

For the purposes of x-y-z ranges, this means that if you want 2 skyboxes to transition when crossing a certain coordinate threshold, you will need to look out for a "gap", as seen in the image above. To avoid this gap, where no sky is shown, you will need to specify the ranges something like this:

**sky1.json**

```json
"yRanges": [
  {"min": -128.0, "max": 150.0}
]
```

**sky2.json**

```json
"yRanges": [
  {"min": 150.0001, "max": 200.0}
]
```

The reason why we don't write `"min": 150.0` in **sky2.json**, is because then both sky 1 and 2 would overlap and show when standing on Y=150. This peculiarity is really only noticeable on the Y coordinate, as it is easy to align the player on exact whole coordinates, but it can also happen on the X and Z coordinates as well, it's just less likely. Even with the method show above, there is still a gap in-between the 2 skyboxes, but it's very unlikely you will manage to align the player that precisely.

### 10.2 Loop object

<table><thead><tr><th width="181" align="center">Name</th><th align="center">Description</th></tr></thead><tbody><tr><td align="center"><code>days</code></td><td align="center">Specifies the length of the loop in days.</td></tr><tr><td align="center"><code>ranges</code></td><td align="center">Specifies the days where the skybox is rendered.</td></tr></tbody></table>

The loop object's start and end points are determined by the fade times in the given skybox. The loop starts at `startFadeIn`, and ends at `endFadeOut` after the specified number of days. To give a concrete example, let's use these parameters:

```json
"properties": {
  "fade": {
    "startFadeIn": 1000,
    "endFadeIn": 2000,
    "startFadeOut": 3000,
    "endFadeOut": 4000
  }
},
"conditions": {
  "loop": {
    "days": 8,
    "ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
  }
}
```

In this scenario, the loop starts at `/time set 1000`. Then if we `/time add 195000` (8×24000+(4000-1000)), that is when the loop will end, and it will start again the next day at time 1000.

See also [MinMax Entry Object](./#9.1-minmax-entry-object) for examples on the implementation.

## 11. Decorations

Stores all specifications for the stars, sun and moon configuration. For optimum results, the moon texture should mimic the vanilla moon texture. The Default value stores the overworld sun and moon textures and sets all enabled to true. All fields are optional.

<table><thead><tr><th width="155" align="center">Name</th><th align="center">Description</th><th align="center">Default</th></tr></thead><tbody><tr><td align="center"><code>blend</code></td><td align="center">Specifies the type of the blend or the decorations only. Valid types are: <code>add</code>, <code>subtract</code>, <code>multiply</code>, <code>screen</code>, <code>replace</code>, <code>alpha</code>, <code>dodge</code>, <code>burn</code>, <code>darken</code>, <code>lighten</code>, <code>decorations</code> and <code>disable</code>.</td><td align="center"><code>decorations</code></td></tr><tr><td align="center"><code>sun</code></td><td align="center">Specifies the location of the texture to be used for rendering the sun.</td><td align="center">Default sun texture (<code>minecraft:textures/environment/sun.png</code>)</td></tr><tr><td align="center"><code>moon</code></td><td align="center">Specifies the location of the texture to be used for rendering the moon.</td><td align="center">Default moon texture (<code>minecraft:textures/environment/moon_phases.png</code>)</td></tr><tr><td align="center"><code>showSun</code></td><td align="center">Specifies whether the sun should be rendered.</td><td align="center"><code>true</code></td></tr><tr><td align="center"><code>showMoon</code></td><td align="center">Specifies whether the moon should be rendered.</td><td align="center"><code>true</code></td></tr><tr><td align="center"><code>showStars</code></td><td align="center">Specifies whether stars should be rendered.</td><td align="center"><code>true</code></td></tr><tr><td align="center"><code>rotation</code></td><td align="center">Specifies the rotation of the decorations.</td><td align="center"><code>[0,0,0]</code> for <code>static</code>/<code>axis</code>/<code>timeShift</code>, <code>0</code> for <code>rotationSpeedX/Y/Z</code>, <code>false</code> for <code>skyboxRotation</code></td></tr></tbody></table>



```json
"decorations": {
  "blend": {"type": "decorations"},
  "sun": "minecraft:textures/environment/sun.png",
  "moon": "minecraft:textures/environment/moon_phases.png",
  "showSun": true,
  "showMoon": true,
  "showStars": true,
  "rotation": {
    "rotationSpeedX": 0.5,
    "static": [0.0, 0.0, 0.0],
    "axis": [0.0, 0.0, 90.0],
    "timeShift": [0, 0, 0],
    "skyboxRotation": "false"
  }
}
```

Rotation in Decorations only affects the sun, moon and stars, and not the skybox. To see how to implement the rotation, check [Rotation Object](./#8.2-rotation-object) and [Float Vector](./#8.2.1-float-vector).

It is worth knowing, that it is possible to specify unique rotation and blending for the sun, moon and stars all individually, if they are set to show `true` in 3 separate json files, and the other 2 decorations are set to show `false`. And you can also add multiple suns and moon with different blending and/or rotation, if you assign them to multiple different sky layers.

## Examples and templates

<details>

<summary>monocolor</summary>

```
{
	"schemaVersion": 2,
	"type": "monocolor",
	"color": {"red": 0.84, "green": 0.91, "blue": 0.72, "alpha": 1.0},
	"blend": {"type" : "add"},
	"properties": {
		"priority": 1,
		"fade": {
			"startFadeIn": 1000,
			"endFadeIn": 2000,
			"startFadeOut": 3000,
			"endFadeOut": 4000,
			"alwaysOn": false
			},
		"rotation": {
			"rotationSpeedY": 0.0,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, -180.0, 0.0]
		},
		"transitionInDuration": 200,
		"transitionOutDuration": 300,
		"changeFog": true,
		"fogColors": {"red": 0.84, "green": 0.91, "blue": 0.97, "alpha": 1.0},
		"sunSkyTint": true,
		"inThickFog": true,
		"maxAlpha": 0.9
	},
	"conditions": {
		"worlds": ["minecraft:overworld"],
		"dimensions": ["my_datapack:custom_world"],
		"weather": ["rain", "thunder"],
		"biomes": ["plains", "forest", "river"],
		"xRanges": [{"min": -100.0, "max": 100.0}],
		"yRanges": [{"min": -128.0, "max": 150.0}, {"min": 200.0, "max": 320.0}],
		"zRanges": [{"min": -150.0, "max": 150.0}],
		"loop": {
			"days": 8,
			"ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
		},
		"effects": ["minecraft:jump_boost", "minecraft:speed", "minecraft:slowness"]
	},
	"decorations": {
		"blend": {"type" : "add"},
		"sun": "minecraft:textures/environment/sun.png",
		"moon": "minecraft:textures/environment/moon_phases.png",
		"showSun": true,
		"showMoon": true,
		"showStars": true,
		"rotation": {
			"rotationSpeedX": 0.5,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, 0.0, 90.0]
		}
	}
}
```

</details>

{% file src=".gitbook/assets/template_monocolor.zip" %}

<details>

<summary>square-textured</summary>

```
{
	"schemaVersion": 2,
	"type": "square-textured",
	"textures": {
		"top": "fabricskyboxes:sky/skybox_top.png",
		"bottom": "fabricskyboxes:sky/skybox_bottom.png",
		"east": "fabricskyboxes:sky/skybox_east.png",
		"west": "fabricskyboxes:sky/skybox_west.png",
		"north": "fabricskyboxes:sky/skybox_north.png",
		"south": "fabricskyboxes:sky/skybox_south.png"
	},
	"blend": {"type" : "alpha"},
	"properties": {
		"priority": 1,
		"fade": {
			"startFadeIn": 1000,
			"endFadeIn": 2000,
			"startFadeOut": 3000,
			"endFadeOut": 4000,
			"alwaysOn": false
			},
		"rotation": {
			"rotationSpeedY": 0.866,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, -180.0, 0.0]
		},
		"transitionInDuration": 200,
		"transitionOutDuration": 300,
		"changeFog": true,
		"fogColors": {"red": 0.84, "green": 0.91, "blue": 0.97, "alpha": 1.0},
		"sunSkyTint": true,
		"inThickFog": true,
		"maxAlpha": 0.9
	},
	"conditions": {
		"worlds": ["minecraft:overworld"],
		"dimensions": ["my_datapack:custom_world"],
		"weather": ["rain", "thunder"],
		"biomes": ["plains", "forest", "river"],
		"xRanges": [{"min": -100.0, "max": 100.0}],
		"yRanges": [{"min": -128.0, "max": 150.0}, {"min": 200.0, "max": 320.0}],
		"zRanges": [{"min": -150.0, "max": 150.0}],
		"loop": {
			"days": 8,
			"ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
		},
		"effects": ["minecraft:jump_boost", "minecraft:speed", "minecraft:slowness"]
	},
	"decorations": {
		"blend": {"type" : "screen"},
		"sun": "minecraft:textures/environment/sun.png",
		"moon": "minecraft:textures/environment/moon_phases.png",
		"showSun": true,
		"showMoon": true,
		"showStars": true,
		"rotation": {
			"rotationSpeedX": 0.5,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, 0.0, 90.0]
		}
	}
}
```

</details>

{% file src=".gitbook/assets/template_square-textured.zip" %}

<details>

<summary>single-sprite-square-textured</summary>

```
{
	"schemaVersion": 2,
	"type": "single-sprite-square-textured",
	"texture": "fabricskyboxes:sky/skybox.png",
	"blend": {"type" : "alpha"},
	"properties": {
		"priority": 1,
		"fade": {
			"startFadeIn": 1000,
			"endFadeIn": 2000,
			"startFadeOut": 3000,
			"endFadeOut": 4000,
			"alwaysOn": false
			},
		"rotation": {
			"rotationSpeedZ": 0.866,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, -180.0, 0.0]
		},
		"transitionInDuration": 200,
		"transitionOutDuration": 300,
		"changeFog": true,
		"fogColors": {"red": 0.84, "green": 0.91, "blue": 0.97, "alpha": 1.0},
		"sunSkyTint": true,
		"inThickFog": true,
		"maxAlpha": 0.9
	},
	"conditions": {
		"worlds": ["minecraft:overworld"],
		"dimensions": ["my_datapack:custom_world"],
		"weather": ["rain", "thunder"],
		"biomes": ["plains", "forest", "river"],
		"xRanges": [{"min": -100.0, "max": 100.0}],
		"yRanges": [{"min": -128.0, "max": 150.0}, {"min": 200.0, "max": 320.0}],
		"zRanges": [{"min": -150.0, "max": 150.0}],
		"loop": {
			"days": 8,
			"ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
		},
		"effects": ["minecraft:jump_boost", "minecraft:speed", "minecraft:slowness"]
	},
	"decorations": {
		"blend": {"type" : "screen"},
		"sun": "minecraft:textures/environment/sun.png",
		"moon": "minecraft:textures/environment/moon_phases.png",
		"showSun": true,
		"showMoon": true,
		"showStars": true,
		"rotation": {
			"rotationSpeedY": 0.5,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, 0.0, 90.0]
		}
	}
}
```

</details>

{% file src=".gitbook/assets/template_single-sprite-square-textured.zip" %}

<details>

<summary>animated-square-textured</summary>

```
{
	"schemaVersion": 2,
	"type": "animated-square-textured",
	"animationTextures": [
		{
			"top": "fabricskyboxes:sky/skybox_frame1_top.png",
			"bottom": "fabricskyboxes:sky/skybox_frame1_bottom.png",
			"east": "fabricskyboxes:sky/skybox_frame1_east.png",
			"west": "fabricskyboxes:sky/skybox_frame1_west.png",
			"north": "fabricskyboxes:sky/skybox_frame1_north.png",
			"south": "fabricskyboxes:sky/skybox_frame1_south.png"
		},
		{
			"top": "fabricskyboxes:sky/skybox_frame2_top.png",
			"bottom": "fabricskyboxes:sky/skybox_frame2_bottom.png",
			"east": "fabricskyboxes:sky/skybox_frame2_east.png",
			"west": "fabricskyboxes:sky/skybox_frame2_west.png",
			"north": "fabricskyboxes:sky/skybox_frame2_north.png",
			"south": "fabricskyboxes:sky/skybox_frame2_south.png"
		},
		{
			"top": "fabricskyboxes:sky/skybox_frame3_top.png",
			"bottom": "fabricskyboxes:sky/skybox_frame3_bottom.png",
			"east": "fabricskyboxes:sky/skybox_frame3_east.png",
			"west": "fabricskyboxes:sky/skybox_frame3_west.png",
			"north": "fabricskyboxes:sky/skybox_frame3_north.png",
			"south": "fabricskyboxes:sky/skybox_frame3_south.png"
		}
	],
	"fps": 4,
	"blend": {"type" : "alpha"},
	"properties": {
		"priority": 1,
		"fade": {
			"startFadeIn": 1000,
			"endFadeIn": 2000,
			"startFadeOut": 3000,
			"endFadeOut": 4000,
			"alwaysOn": false
			},
		"rotation": {
			"rotationSpeedX": 0.866,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, -180.0, 0.0]
		},
		"transitionInDuration": 200,
		"transitionOutDuration": 300,
		"changeFog": true,
		"fogColors": {"red": 0.84, "green": 0.91, "blue": 0.97, "alpha": 1.0},
		"sunSkyTint": true,
		"inThickFog": true,
		"maxAlpha": 0.9
	},
	"conditions": {
		"worlds": ["minecraft:overworld"],
		"dimensions": ["my_datapack:custom_world"],
		"weather": ["rain", "thunder"],
		"biomes": ["plains", "forest", "river"],
		"xRanges": [{"min": -100.0, "max": 100.0}],
		"yRanges": [{"min": -128.0, "max": 150.0}, {"min": 200.0, "max": 320.0}],
		"zRanges": [{"min": -150.0, "max": 150.0}],
		"loop": {
			"days": 8,
			"ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
		},
		"effects": ["minecraft:jump_boost", "minecraft:speed", "minecraft:slowness"]
	},
	"decorations": {
		"blend": {"type" : "add"},
		"sun": "minecraft:textures/environment/sun.png",
		"moon": "minecraft:textures/environment/moon_phases.png",
		"showSun": true,
		"showMoon": true,
		"showStars": true,
		"rotation": {
			"rotationSpeedZ": 0.5,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, 0.0, 90.0]
		}
	}
}
```

</details>

{% file src=".gitbook/assets/template_animated-square-textured.zip" %}

<details>

<summary>single-sprite-animated-square-textured</summary>

```
{
	"schemaVersion": 2,
	"type": "single-sprite-animated-square-textured",
	"animationTextures": [
		"fabricskyboxes:sky/skybox_frame1.png",
		"fabricskyboxes:sky/skybox_frame2.png",
		"fabricskyboxes:sky/skybox_frame3.png"
	],
	"fps": 4,
	"blend": {"type" : "alpha"},
	"properties": {
		"priority": 1,
		"fade": {
			"startFadeIn": 1000,
			"endFadeIn": 2000,
			"startFadeOut": 3000,
			"endFadeOut": 4000,
			"alwaysOn": false
			},
		"rotation": {
			"rotationSpeedY": 0.866,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, -180.0, 0.0]
		},
		"transitionInDuration": 200,
		"transitionOutDuration": 300,
		"changeFog": true,
		"fogColors": {"red": 0.84, "green": 0.90, "blue": 0.97, "alpha": 1.0},
		"sunSkyTint": true,
		"inThickFog": true,
		"maxAlpha": 0.9
	},
	"conditions": {
		"worlds": ["minecraft:overworld"],
		"dimensions": ["my_datapack:custom_world"],
		"weather": ["rain", "thunder"],
		"biomes": ["plains", "forest", "river"],
		"xRanges": [{"min": -100.0, "max": 100.0}],
		"yRanges": [{"min": -128.0, "max": 150.0}, {"min": 200.0, "max": 320.0}],
		"zRanges": [{"min": -150.0, "max": 150.0}],
		"loop": {
			"days": 8,
			"ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
		},
		"effects": ["minecraft:jump_boost", "minecraft:speed", "minecraft:slowness"]
	},
	"decorations": {
		"blend": {"type" : "screen"},
		"sun": "minecraft:textures/environment/sun.png",
		"moon": "minecraft:textures/environment/moon_phases.png",
		"showSun": true,
		"showMoon": true,
		"showStars": true,
		"rotation": {
			"rotationSpeedZ": 0.5,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, 0.0, 90.0]
		}
	}
}
```

</details>

{% file src=".gitbook/assets/template_single-sprite-animated-square-textured.zip" %}

<details>

<summary>multi-texture</summary>

```
{
	"schemaVersion": 2,
	"type": "multi-texture",
	"animations": [
		{
			"texture": "fabricskyboxes:sky/animation.png",
			"uvRanges": {
				"minU": 0.416666666,
				"minV": 0.5,
				"maxU": 0.583333333,
				"maxV": 0.75
			},
			"gridColumns": 2,
			"gridRows": 3,
			"duration": 500
		}
	],
	"blend": {"type" : "alpha"},
	"properties": {
		"priority": 1,
		"fade": {
			"startFadeIn": 1000,
			"endFadeIn": 2000,
			"startFadeOut": 3000,
			"endFadeOut": 4000,
			"alwaysOn": false
			},
		"rotation": {
			"rotationSpeedZ": 0.866,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, -180.0, 0.0]
		},
		"transitionInDuration": 200,
		"transitionOutDuration": 300,
		"changeFog": true,
		"fogColors": {"red": 0.84, "green": 0.91, "blue": 0.97, "alpha": 1.0},
		"sunSkyTint": true,
		"inThickFog": true,
		"maxAlpha": 0.9
	},
	"conditions": {
		"worlds": ["minecraft:overworld"],
		"dimensions": ["my_datapack:custom_world"],
		"weather": ["rain", "thunder"],
		"biomes": ["plains", "forest", "river"],
		"xRanges": [{"min": -100.0, "max": 100.0}],
		"yRanges": [{"min": -128.0, "max": 150.0}, {"min": 200.0, "max": 320.0}],
		"zRanges": [{"min": -150.0, "max": 150.0}],
		"loop": {
			"days": 8,
			"ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
		},
		"effects": ["minecraft:jump_boost", "minecraft:speed", "minecraft:slowness"]
	},
	"decorations": {
		"blend": {"type" : "screen"},
		"sun": "minecraft:textures/environment/sun.png",
		"moon": "minecraft:textures/environment/moon_phases.png",
		"showSun": true,
		"showMoon": true,
		"showStars": true,
		"rotation": {
			"rotationSpeedY": 0.5,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, 0.0, 90.0]
		}
	}
}
```

</details>

{% file src=".gitbook/assets/template_multi-texture.zip" %}
