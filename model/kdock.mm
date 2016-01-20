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
    att value: Continuous
}
