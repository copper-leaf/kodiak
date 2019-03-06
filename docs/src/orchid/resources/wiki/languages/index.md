---
---

{% set childPages = index.findChildPages(page) %}

{% for childPage in childPages %}
- [{{childPage.title}}]({{childPage.link}})
{% endfor %}