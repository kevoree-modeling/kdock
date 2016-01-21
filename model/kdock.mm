class kdock.Host {
    att name: String with index
    rel containers: kdock.Container
}

class kdock.Container {
    att id: String
    att name: String
    rel metrics: kdock.Metric
}

class kdock.Metric {
    att name: String
    rel values : kdock.Value
    rel metrics : kdock.Metric
}

class kdock.Value {
    att value: Continuous with precision 1
}

