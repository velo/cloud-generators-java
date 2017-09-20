# Kentico Cloud model generator utility for Java

[![Build Status](https://travis-ci.org/Kentico/cloud-generators-java.svg?branch=master)](https://travis-ci.org/Kentico/cloud-generators-java)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![SonarQube](http://img.shields.io/badge/SonarQube-Results-blue.svg)](https://sonarcloud.io/organizations/kentico-github/projects)

🚧 **This is currently a work in progress, and is only a prototype. Not ready for general consumption. Let us know if you want to contribute at DEVELOPERSCOMMUNITY@KENTICO.COM** 🚧 

This utility generates strongly-typed models based on Content Types in a Kentico Cloud project. The models are supposed to be used together with the [Kentico Cloud Delivery SDK for Java](https://github.com/Kentico/delivery-sdk-java). Please read the [documentation](https://github.com/Kentico/delivery-sdk-java/wiki/Working-with-Strongly-Typed-Models-(aka-Code-First-Approach)#customizing-the-strong-type-binding-logic) to see all benefits of this approach.

## How to use

### Gradle

Apply the gradle plugin to your build file.

```groovy
buildscript {
	repositories {
		mavenCentral()
		maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
	}
	dependencies {
		classpath("com.kenticocloud:cloud-generators-gradle:1.0-SNAPSHOT")
	}
}

apply plugin: 'java'
apply plugin: 'com.kenticocloud.generator'

kenticoModel {
	projectId = '975bf280-fd91-488c-994c-2f04416e5ee3'
	packageName = 'com.dancinggoat.models'
	outputDir = file('generated-sources')
}

dependencies {
    compile('com.kenticocloud:delivery-sdk-java:1.0.1')
}
```

To generate sources, run the 'generateModels' task.

```
./gradlew generateModels
```

If you have the 'java' plugin applied to your project, the plugin will automatically register your outputDir as a srcDir
with it.

The plugin does *not* however automatically attach itself to the build task graph, so if you desire to add it, that has
to be done manually.  Note however, it may not be desirable to do so, as the plugin makes a call to the Kentico API 
which may increase build time.  It is recommended you check your generated sources in.

If you are an IntelliJ user, you can hint to IntelliJ that a warning should be provided when editing the generated 
sources by adding the following:

```groovy
apply plugin: 'idea'

idea {
	module {
		// Marks the already(!) added srcDir as "generated"
		generatedSourceDirs += file('generated-sources')
	}
}
```

### Parameters
- `projectId` - required - a GUID that can be found in [Kentico Cloud](https://app.kenticocloud.com) -> API keys -> Project ID
- `packageName` - required - a name of the Java package to put your generated sources into
- `outputDir` - required - a File object instance referencing an output folder path

## Example output

```java
package com.dancinggoat.models;

import com.kenticocloud.delivery.Asset;
import com.kenticocloud.delivery.ContentItem;
import com.kenticocloud.delivery.ContentItemMapping;
import com.kenticocloud.delivery.ElementMapping;
import com.kenticocloud.delivery.Taxonomy;
import java.lang.String;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * This code was generated by a <a href="https://github.com/Kentico/cloud-generators-java">cloud-generators-java tool</a>
 *
 * Changes to this file may cause incorrect behavior and will be lost if the code is regenerated.
 * For further modifications of the class, create a separate file and extend this class.
 */
@ContentItemMapping("article")
public class Article {
  @ElementMapping("personas")
  List<Taxonomy> personas;

  @ElementMapping("title")
  String title;

  @ElementMapping("teaser_image")
  List<Asset> teaserImage;

  @ElementMapping("post_date")
  ZonedDateTime postDate;

  @ElementMapping("summary")
  String summary;

  @ElementMapping("body_copy")
  String bodyCopy;

  @ElementMapping("related_articles")
  ContentItem relatedArticles;

  @ElementMapping("meta_keywords")
  String metaKeywords;

  @ElementMapping("meta_description")
  String metaDescription;

  public List<Taxonomy> getPersonas() {
    return personas;
  }

  public void setPersonas(List<Taxonomy> personas) {
    this.personas = personas;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<Asset> getTeaserImage() {
    return teaserImage;
  }

  public void setTeaserImage(List<Asset> teaserImage) {
    this.teaserImage = teaserImage;
  }

  public ZonedDateTime getPostDate() {
    return postDate;
  }

  public void setPostDate(ZonedDateTime postDate) {
    this.postDate = postDate;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getBodyCopy() {
    return bodyCopy;
  }

  public void setBodyCopy(String bodyCopy) {
    this.bodyCopy = bodyCopy;
  }

  public ContentItem getRelatedArticles() {
    return relatedArticles;
  }

  public void setRelatedArticles(ContentItem relatedArticles) {
    this.relatedArticles = relatedArticles;
  }

  public String getMetaKeywords() {
    return metaKeywords;
  }

  public void setMetaKeywords(String metaKeywords) {
    this.metaKeywords = metaKeywords;
  }

  public String getMetaDescription() {
    return metaDescription;
  }

  public void setMetaDescription(String metaDescription) {
    this.metaDescription = metaDescription;
  }
}
```

## Feedback & Contributing
Check out the [contributing](https://github.com/Kentico/cloud-generators-java/blob/master/CONTRIBUTING.md) page to see the best places to file issues, start discussions and begin contributing.

## Further information

For more developer resources, visit the Kentico Cloud Developer Hub at <https://developer.kenticocloud.com>.

### Building the sources

Prerequisites:

**Required:**
Java 8 SDK (Oracle & OpenJDK both tested and supported)

Ensure your `JAVA_HOME` environment is set.  Then build the project via the provided Gradle wrapper.
```
./gradlew clean build
```

Optional:
[JetBrains IntelliJ Idea](https://www.jetbrains.com/idea/) project files are included.  Open up the project and Import the Gradle project to sync up dependencies.
 * Note: The Gradle Test Kit in the cloud-generators-gradle module requires some extra steps to ensure it can access the 
 classpath of the plugin, which are handled in the gradle build.  When running the unit test from inside IntelliJ, you 
 may need to run the gradle build prior to ensure the latest build output is used if you are changing the main sources.
