package com.copperleaf.kodiak

/**
 * Generators are what create the output pages within Orchid. Generators are run after all Options have been processed
 * as part of a OrchidTask. When a OrchidTask is chosen that builds the output site, it happens in two phases: the **indexing
 * phase** and the **generation phase**.
 *
 * ### Indexing Phase
 * During the indexing phase, Generators should determine exactly the output pages they intend to generate, returning an
 * index of those pages. This index should consist of a JSONArray or JSONObject, wrapped in a JSONElement. The content
 * of either should be JSONObjects (either as a list or as a hierarchy) which contain at least the following properties:
 *
 * * 'name' - the display name of the page
 * * 'url' - the url of the target page. This should be absolute using the set baseUrl OrchidOption, which is done automatically
 * by OrchidReference
 *
 * This JSONElement is then placed into the root JSONObject under the key specified by `getKey()`. At generation time,
 * this index will be used to create deep-links throughout the site, typically through the page navigation or from
 * manually creating links, either through Javadoc 'see' or 'link' tags, or using an output filter in the main template.
 *
 * It is important that **no pages be written during the Indexing phase**. The sites's index is not completed at this
 * point, so it is likely that navigation among pages will not work as expected.
 *
 * ### Generation Phase
 * During the Generation phase, Generators can start to write their pages. Typically, this step involves picking a
 * template for an OrchidPage and generating a page according to that template. This template should be provided by the
 * Theme, but if the Theme did not implement the desired template, Orchid will attempt to find a suitable fallback.
 *
 * It is also worth noting that templates can be provided by OrchidGenerator extensions apart from a Theme, and when
 * using Pebble as the main template engine, can be injected into the site's content by extending one of the Theme's
 * layouts, which should typically be 'templates/layouts/index.peb'.
 *
 * @since v1.0.0
 * @orchidApi extensible
 */
class KotlinMarkdown {



}