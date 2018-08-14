# Building process

```
                         /-----------\    evaluate(override?)
  build(overrides) --->  |   init    |  --------------------> [Attribute]
                         \-----------/  <-------------------------/ 
                               |   
                               | - onAfterInit(attributes)
                               | - traits.each(it.onAfterInit(attributes))
                               |
                               | attributes (Map<String, Object>)
                               V
                         /-----------\
                         | construct |
                         \-----------/
                               |
                               | object (T)
                               V
                         /-----------\    evaluate(object, override?)
                         | finalize  |  --------------------> [Attribute]
                         \-----------/  <-------------------------/
                               |
                               | - onAfterBuild(object)
                               | - traits.each(it.onAfterBuild(object))
                               |
    build(object) ---------\   | object (T)
                           V   V
        object           /-----------\
  <-------------------   |  persist  |  
                         \-----------/
```